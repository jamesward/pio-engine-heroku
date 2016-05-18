import akka.actor.ActorSystem
import akka.io.IO
import io.prediction.controller.Engine
import io.prediction.data.storage.EngineManifest
import io.prediction.workflow.{CreateServer, ServerConfig, WorkflowUtils}
import spray.can.Http
import spray.can.server.ServerSettings

object ServerApp extends App {

  val port = sys.env.getOrElse("PORT", "8000").toInt
  val eventServerIp = sys.env.getOrElse("EVENT_SERVER_IP", "localhost")
  val eventServerPort = sys.env.getOrElse("EVENT_SERVER_PORT", "7070").toInt
  val maybeAccessKey = sys.env.get("ACCESS_KEY")

  val maybeLatestEngineInstance = CreateServer.engineInstances.getLatestCompleted(EngineConfig.engineId, EngineConfig.engineVersion, EngineConfig.engineVariantId)

  maybeLatestEngineInstance.map { engineInstance =>
    // the spark config needs to be set in the engineInstance
    engineInstance.copy(sparkConf = engineInstance.sparkConf.updated("spark.master", "local"))
  }.fold {
    println("Could not get latest completed engine instance")
  } { engineInstance =>

    val sc = ServerConfig(
      engineInstanceId = engineInstance.id,
      engineId = Some(engineInstance.engineId),
      engineVersion = Some(engineInstance.engineVersion),
      engineVariant = EngineConfig.engineVariant,
      port = port,
      eventServerIp = eventServerIp,
      eventServerPort = eventServerPort,
      accessKey = maybeAccessKey
    )

    val (engineLanguage, engineFactory) = WorkflowUtils.getEngine(engineInstance.engineFactory, getClass.getClassLoader)
    val engine = engineFactory()

    // WTF: The list of files must be at least 2 long due to https://github.com/PredictionIO/PredictionIO/blob/v0.9.6/core/src/main/twirl/io/prediction/workflow/index.scala.html#L56
    val manifest = EngineManifest(engineInstance.engineId, engineInstance.engineVersion, "default", None, Seq("WHAT", "THE"), engineInstance.engineFactory)

    val actor = CreateServer.createServerActorWithEngine(
      sc,
      engineInstance,
      engine.asInstanceOf[Engine[_, _, _, _, _, _]],
      engineLanguage,
      manifest
    )

    val actorSystem = ActorSystem("pio-server")

    val settings = ServerSettings(actorSystem)

    IO(Http)(actorSystem) ! Http.Bind(listener = actor, interface = sc.ip, port = sc.port)

    actorSystem.awaitTermination()
  }

}

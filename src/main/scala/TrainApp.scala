import io.prediction.workflow.CreateWorkflow

object TrainApp extends App {

  // todo: non-global
  sys.props.put("spark.master", "local")

  // WTF: envs must not be empty or CreateServer.engineInstances.get... fails due to JDBCUtils.stringToMap
  val envs = "FOO=BAR"

  CreateWorkflow.main(Array("--engine-id", EngineConfig.engineId, "--engine-version", EngineConfig.engineVersion, "--engine-variant", EngineConfig.engineVariant, "--env", envs))

}

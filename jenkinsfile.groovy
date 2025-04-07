pipeline {
  agent any

  tools {
    nodejs 'nodejs'
  }

  environment {
    PROJECT_ROOT = 'src'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/DrDiomedes/sonar-qube.git'
      }
    }
    stage('SonarQube scan') {
      environment {
        scannerHome = tool 'sonar-scanner'
      }
      steps {
        withSonarQubeEnv('sonarqube') {
          sh """
            ${scannerHome}/bin/sonar-scanner \
              -Dsonar.projectKey=prueba-pipeline \
              -Dsonar.projectName=SonarPipeline \
              -Dsonar.projectVersion=1.0 \
              -Dsonar.sources=. \
              -Dsonar.login=Javier_Alarcon \
              -Dsonar.password=&.UocnjF4<FZ \
              -Dsonar.host.url=http://a63624d9132de488682b9fd86a811aa8-550206468.us-east-2.elb.amazonaws.com/sonarqube/sessions/new?return_to=%2Fsonarqube%2F
          """,
          returnStatus: true  
        }

        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }
}

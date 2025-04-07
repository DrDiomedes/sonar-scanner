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
              -Dsonar.host.url=http://localhost:9000
          """
        }

        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }
}

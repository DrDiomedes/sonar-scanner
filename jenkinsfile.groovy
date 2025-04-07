pipeline {
  agent any

  tools {
    nodejs 'nodejs'
  }

  environment {
    PROJECT_ROOT = '.'
    SONARQUBE_URL = 'http://a63624d9132de488682b9fd86a811aa8-550206468.us-east-2.elb.amazonaws.com/sonarqube'
    SONARQUBE_LOGIN = 'Javier_Alarcon'
    SONARQUBE_PASSWORD = '&.UocnjF4<FZ'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/DrDiomedes/sonar-qube.git'
      }
    }

    stage('Ejecutar escaneo SonarQube') {
      environment {
        scannerHome = tool 'sonar-scanner'
      }
      steps {
        withSonarQubeEnv('sonarqube') {
          sh """
            ${scannerHome}/bin/sonar-scanner \
              -Dsonar.projectKey=prueba-pipeline \
              -Dsonar.projectName=SonarPipeline \
              -Dsonar.projectVersion=1.0.${BUILD_NUMBER} \
              -Dsonar.sources=${PROJECT_ROOT} \
              -Dsonar.login=${SONARQUBE_LOGIN} \
              -Dsonar.password='${SONARQUBE_PASSWORD}' \
              -Dsonar.host.url=${SONARQUBE_URL}
          """
        }
      }
    }

    stage('Verificar resultado del análisis') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }
}

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

    stage('Install deps') {
      steps {
        sh "npm install"
      }
    }

    stage('Test') {
      steps {
        sh "npm run test"
      }
    }

    stage('Coverage') {
      steps {
        sh "npm run coverage"
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
              -Dsonar.sources=./${PROJECT_ROOT} \
              -Dsonar.tests=./${PROJECT_ROOT}/test \
              -Dsonar.javascript.lcov.reportPaths=./${PROJECT_ROOT}/coverage/lcov.info \
              -Dsonar.projectVersion=1.0.${BUILD_NUMBER} \
              -Dsonar.login=Javier_Alarcon \
              -Dsonar.password=&.UocnjF4<FZ \
              -Dsonar.host.url=http://ado.sonar.com:9000
          """
        }

        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }
}

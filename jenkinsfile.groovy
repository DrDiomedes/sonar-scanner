pipeline {
  agent any

  tools {
    nodejs 'nodejs'
  }

  environment {
    PROJECT_ROOT = '.'
    SONARQUBE_URL = 'http://sonarqube.sonarqube.svc:9000/sonarqube'
    PROJECT = 'https://github.com/veracode/verademo.git'
  }

  stages {
    stage('Checkout') {
      steps {
        /// git branch: 'main', url: 'https://github.com/DrDiomedes/sonar-qube.git'
        git branch: 'main', url: 'https://github.com/veracode/verademo.git'
      }
    }

    stage('Ejecutar escaneo SonarQube') {
      environment {
        scannerHome = tool 'sonar-scanner'
      }
      steps {
        script{  
          withSonarQubeEnv('sonarqube') {
            withCredentials([string(credentialsId: '31aa0c79-c552-4d6c-9c22-fd5e646438ad', variable: 'SONAR_TOKEN')]) {
            def repoUrl = PROJECT
            def appname = repoUrl.tokenize('/').last().replace('.git', '')  
            echo "Aplicación identificada: ${appname}"
            def buildid = env.BUILD_ID
            def commitcode = env.GIT_COMMIT
            def timestamp = new Date().format("yyyyMMdd-HHmm")
            def scanversion = "${appname}-${commitcode}-${timestamp}"  
              sh """
                ${scannerHome}/bin/sonar-scanner \
                  -Dsonar.projectKey=${appname} \
                  -Dsonar.projectName=${appname} \
                  -Dsonar.projectVersion=${scanversion} \
                  -Dsonar.exclusions=**/*.java \
                  -Dsonar.sources=${PROJECT_ROOT} \
                  -Dsonar.token=${SONAR_TOKEN} \
                  -Dsonar.host.url=${SONARQUBE_URL}
              """
              sh """
              curl -i http://sonarqube.sonarqube.svc:9000/sonarqube
              """
            }
          }
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

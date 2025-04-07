pipeline {
  agent any

  tools {
    nodejs 'nodejs'
  }

  environment {
    // No estás usando src/, así que mejor usar la raíz o eliminar esto
    PROJECT_ROOT = '.'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/DrDiomedes/sonar-qube.git'
      }
    }

    stage('Validar conexión a SonarQube') {
      environment {
        scannerHome = tool 'sonar-scanner'
      }
      steps {
        withSonarQubeEnv('sonarqube') {
          script {
            def status = sh(
              script: """
                ${scannerHome}/bin/sonar-scanner \
                  -Dsonar.projectKey=prueba-pipeline \
                  -Dsonar.projectName=SonarPipeline \
                  -Dsonar.projectVersion=1.0 \
                  -Dsonar.sources=. \
                  -Dsonar.login=Javier_Alarcon \
                  -Dsonar.password='&.UocnjF4<FZ' \
                  -Dsonar.host.url=http://a63624d9132de488682b9fd86a811aa8-550206468.us-east-2.elb.amazonaws.com/sonarqube
              """,
              returnStatus: true
            )

            if (status == 0) {
              echo "✅ Login exitoso a SonarQube"
            } else {
              echo "❌ Login fallido a SonarQube"
              error("Falló la autenticación con SonarQube")
            }
          }
        }
      }
    }
  }
}

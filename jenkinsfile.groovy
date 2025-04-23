@Library('devsecops_library') _

pipeline {
  agent any
  environment {
    PROJECT = 'https://github.com/veracode/verademo.git'
    PROJECT_ROOT = 'app/'
    SONARQUBE_URL = 'http://sonarqube.sonarqube.svc:9000/sonarqube'
  }

  stages {
    stage('Checkout') {
      steps {
        dir ('app'){
          echo "01 - INICIA CLONADO DEL REPOSITORIO DEL CODIGO "
          git branch: 'main', url: "${PROJECT}"
        }
      }
    }

    stage('Análisis SonarQube') {
      steps {
        echo "02 - INICIA ESCANEO DE CALIDAD DE CODIGO CON SONARQUBE "
        sonarScan()
        
      }
    }

    stage('Análisis Semgrep') {
      steps {
        echo "03 - INICIA ESCANEO ESTATICO DEL CODIGO CON SEMGREP "
        sh '''
        
          echo "Descargando y ejecutando análisis Semgrep..."
          mkdir -p semgrep-rules
          curl -sSL https://semgrep.dev/c/p/java -o semgrep-rules/java.yml
          curl -sSL https://semgrep.dev/c/p/security-audit -o semgrep-rules/security-audit.yml
          curl -sSL https://semgrep.dev/c/p/owasp-top-ten -o semgrep-rules/owasp-top-ten.yml
          echo "Ejecutando análisis Semgrep..."
          semgrep scan ${PROJECT_ROOT} \
            --config semgrep-rules/java.yml \
            --config semgrep-rules/security-audit.yml \
            --config semgrep-rules/owasp-top-ten.yml \
            --metrics=off \
            --timeout-threshold 10000 \
            --json-output semgrep-result.json
          ls -la
        '''
        archiveArtifacts artifacts: 'semgrep-result.json', allowEmptyArchive: true
      }
    }
  }
}

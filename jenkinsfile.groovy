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
        sastScan()
        

        /*
        echo "03 - INICIA ESCANEO ESTATICO DEL CODIGO CON SEMGREP "
        def repoUrl = config.repo ?: env.PROJECT
        def appname = repoUrl.tokenize('/').last().replace('.git', '')
        def buildid = env.BUILD_ID
        def commitcode = env.GIT_COMMIT
        def timestamp = new Date().format("yyyyMMdd-HHmm")
        def scanversion = "${appname}-${commitcode}-${timestamp}"
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
            --json-output sast-${scanversion}.json
          ls -la
        '''
        archiveArtifacts artifacts: 'semgrep-result.json', allowEmptyArchive: true
        */
      }
    }
  }
}

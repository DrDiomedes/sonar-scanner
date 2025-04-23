@Library('devsecops_library') _

pipeline {
  agent any
  environment {
    PROJECT = 'https://github.com/veracode/verademo.git'
    PROJECT_ROOT = '.'
    SONARQUBE_URL = 'http://sonarqube.sonarqube.svc:9000/sonarqube'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: "${PROJECT}"
      }
    }

    stage('Análisis SonarQube') {
      steps {
        //sonarScan()
      }
    }

    stage('Análisis Semgrep') {
      steps {
        sh '''
        
          echo "Descargando y ejecutando análisis Semgrep..."
          mkdir -p semgrep-rules
          curl -sSL https://semgrep.dev/c/p/java -o semgrep-rules/java.yml
          curl -sSL https://semgrep.dev/c/p/java-spring -o semgrep-rules/java-spring.yml
          curl -sSL https://semgrep.dev/c/p/security-audit -o semgrep-rules/security-audit.yml
          curl -sSL https://semgrep.dev/c/p/owasp-top-ten -o semgrep-rules/owasp-top-ten.yml
          ls -la
          echo "Ejecutando análisis Semgrep..."
          semgrep --version 
          semgrep scan ${PROJECT_ROOT} \
            --config semgrep-rules/	\
            --metrics=off \
            --timeout-threshold 10000 \
            --json-output semgrep-result.json
            --verbose
        '''
      }
    }
  }
}

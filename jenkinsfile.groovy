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
        git branch: 'main', url: "${PROJECT}"
      }
    }

    stage('Análisis SonarQube') {
      steps {
        //sonarScan()
        echo "OMITIENDO SONAR"
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
          ls -la app
          echo "Ejecutando análisis Semgrep..."
          curl -I https://semgrep.dev 
          semgrep scan ${PROJECT_ROOT} \
            --config semgrep-rules/java.yml \
            --config semgrep-rules/java-spring.yml \
            --config semgrep-rules/security-audit.yml \
            --config semgrep-rules/owasp-top-ten.yml \
            --metrics=off \
            --timeout-threshold 10000 \
            --verbose
        '''
      }
    }
  }
}

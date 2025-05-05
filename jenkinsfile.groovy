@Library('devsecops_library') _

pipeline {
  agent any
  environment {
    PROJECT = 'https://github.com/veracode/verademo.git'
    PROJECT_ROOT = '.'
    SONARQUBE_URL = 'http://sonarqube.sonarqube.svc:9000/sonarqube'
    DEFECTOJO_URL = 'http://defectdojo-django.defectdojo.svc'
 
  }
  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: "${PROJECT}"
      }
    }

    stage('Análisis SonarQube') {
      steps {
        sonarScan()
      }
    }

    stage('Análisis Semgrep') {
      steps {
        sastScan()
      }
    }   

    stage('registro DefectDojo') {
      steps {
        sh """
          file=$(ls sast-*.json | head -n 1)
          echo "Archivo detectado: $file"
      
          curl -X POST "curl http://defectdojo-django.defectdojo.svc/api/v2/import-scan/" \
            -H "Authorization: Token 5a79a17492584808dc2407325923269a6d3df3b6" \
            -F 'scan_type=Semgrep JSON Report' \
            -F 'engagement=Semgrep Scan' \
            -F 'file=@sast-verademo-<algo>.json' \
            -F 'active=true' \
            -F 'verified=true' \
            -F 'scan_date='$(date +%Y-%m-%d) \
            -F 'minimum_severity=Low'
        
        """
        
      }
    }  
  }
}

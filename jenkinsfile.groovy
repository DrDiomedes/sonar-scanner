@Library('devsecops_library') _

pipeline {
  agent any
  environment {
    PROJECT = 'https://github.com/veracode/verademo.git'
    PROJECT_ROOT = '.'
    SONARQUBE_URL = 'http://sonarqube.sonarqube.svc:9000/sonarqube'
  }
  stages {
    stage('01 - Checkout') {
      steps {
        git branch: 'main', url: "${PROJECT}"
      }
    }

    stage('02 - Anlisis SonarQube') {
      steps {
       sonarScan()
      }
    }

    stage('03 - Análisis Estatico') {
      steps {
        sastScan()
      }
    }
    
    stage('04 - Registro DefectDojo') {
      steps {
        defectdojoRegistry()
        
      }
    }    
  }
}

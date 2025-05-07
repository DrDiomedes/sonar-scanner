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

    stage('Anlisis SonarQube') {
      steps {
       sonarScan()
      }
    }

    stage('Análisis Estatico') {
      steps {
        sastScan()
      }
    }
    
    stage('Registro DefectDojo') {
      steps {
        defectdojoRegistry()
        
      }
    }    
  }
}

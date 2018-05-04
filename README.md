# Gradle dependencie

## Root
allprojects {

		repositories {
		
			...
			
			maven { url 'https://jitpack.io' }
			
		}
		
	}
  
  ## App
  dependencies {
  
	        implementation 'com.github.azizcse:rmmodule:0.0.2'
		
	}

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
	

## How to use this module

In Unity UnityPlayerActivity you need to add below code 

 private IRMAidlInterface mBinder;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = IRMAidlInterface.Stub.asInterface(service);
            try {
                mBinder.setForeground(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(mBinder != null){
                mBinder = null;
            }
        }
    };
    
   
   /**
     * Handle creating the service intent and binding to it in a reusable function.
     */
     
    private void connectToService() {
        Intent serviceIntent = new Intent(this, RMService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    /**
     * Unbinds from app service and sets {@link this#mBinder} to null.
     */
    private void disconnectFromService() {
        if (mBinder != null) {
            try {
                mBinder.setForeground(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
            mBinder = null;
        }
    }
    
    
    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        connectToService();
        mUnityPlayer.resume();
    }
    
     // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        disconnectFromService();
        mUnityPlayer.pause();
    } 
     // onStop Unity
    @Override protected void onStop()
    {
        super.onStop();
        disconnectFromService();
        mUnityPlayer.stop();
    }




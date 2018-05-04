package com.w3engineers.rmunity.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.w3engineers.rmunity.service.RMService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;
import io.reactivex.functions.Consumer;

import static io.left.rightmesh.mesh.PeerListener.ADDED;
import static io.left.rightmesh.mesh.PeerListener.REMOVED;
import static io.left.rightmesh.mesh.PeerListener.UPDATED;

/**
 * Created by USER22 on 4/24/2018.
 */

public class RMController implements MeshStateListener {
    // Port to bind app to.
    private static final int MESH_PORT = 45667;
    // MeshManager instance - interface to the mesh network.
    private AndroidMeshManager meshManager = null;

    // Set to keep track of peers connected to the mesh.
    private HashSet<MeshID> discovered = new HashSet<>();
    private String userInformation;
    private Map<String, MeshID> discoveredId = new HashMap<>();
    private RMService rmService;

    private String myUserId;

    public RMController(RMService rmService){
        this.rmService = rmService;
    }
    /**
     * Get a {@link AndroidMeshManager} instance, starting RightMesh if it isn't already running.
     *
     * @param context service context to bind to
     */
    public void connect(Context context) {
        meshManager = AndroidMeshManager.getInstance(context, RMController.this,"Unitymodule");
    }

    /**
     * Close the RightMesh connection, stopping the service if no other apps are running.
     */
    public void disconnect() {
        try {
            if (meshManager != null) {
                meshManager.stop();
            }
        } catch (MeshService.ServiceDisconnectedException ignored) {
            // Error encountered shutting down service - nothing we can do from here.
        }
    }

    @Override
    public void meshStateChanged(MeshID meshID, int state) {
        if (state == SUCCESS) {
            myUserId = meshID.toString();

            sendMyUserId(myUserId);

            discoveredId.put(myUserId, meshID);
            try {
                meshManager.bind(MESH_PORT);
            } catch (RightMeshException e) {
                e.printStackTrace();
            }

            meshManager.on(MeshManager.PEER_CHANGED, new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                    handlePeerChanged((MeshManager.RightMeshEvent) o);
                }
            });

            meshManager.on(MeshManager.DATA_RECEIVED, new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                    handleDataReceived((MeshManager.RightMeshEvent) o);
                }
            });
        }
    }

    private void handlePeerChanged(MeshManager.RightMeshEvent e) {
        // Update peer list.
        MeshManager.PeerChangedEvent event = (MeshManager.PeerChangedEvent) e;

        // Ignore ourselves.
        if (event.peerUuid.equals(meshManager.getUuid())) {
            return;
        }

        String userId = event.peerUuid.toString();

        String userJsonInfo = buildJsonString(event.state, userId);

        if(rmService != null) {
            rmService.sendToUnity(userJsonInfo.getBytes());
        }
        if(event.state == ADDED){
            discoveredId.put(userId, event.peerUuid);

        }else if(event.state == UPDATED){
            if(!discoveredId.containsKey(userId)){
                discoveredId.put(userId, event.peerUuid);
            }

        }else if(event.state == REMOVED){
            discoveredId.remove(userId);
        }
    }

    private String buildJsonString(int event, String id){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", event);
            jsonObject.put("userId", id);
            return jsonObject.toString();
        }catch (JSONException e){
        }

        return null;
    }

    private void handleDataReceived(MeshManager.RightMeshEvent event) {
        MeshManager.DataReceivedEvent dataReceivedEvent = (MeshManager.DataReceivedEvent) event;
        if(rmService != null) {
            Log.e("DataSend","Send data to Unity");
            rmService.sendToUnity(dataReceivedEvent.data);
        }
    }

    public void sendData(String id, String data){
        MeshID meshID = discoveredId.get(id);
        if(meshID != null) {
            Log.e("DataSend","MeshId ="+meshID.toString());
            sendData(meshID, data);
        }else {
            Log.e("DataSend","MeshId null");
        }
    }
    private void sendData(MeshID meshID, String data){

        try {
            Log.e("DataSend","Send data to Mesh API");
            meshManager.sendDataReliable(meshID, MESH_PORT, data.getBytes());
        } catch (RightMeshException e) {
            e.printStackTrace();
        }
    }

    private void sendMyUserId(String myUserId){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", myUserId);
            jsonObject.put("type", 4);// 4 my user id
            if(rmService != null) {
                rmService.sendToUnity(jsonObject.toString().getBytes());
            }
        }catch (JSONException e){}
    }

    public void sendToNetwork(String data) {
        userInformation = data;
    }
}

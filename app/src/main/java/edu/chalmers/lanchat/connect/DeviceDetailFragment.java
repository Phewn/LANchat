/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.chalmers.lanchat.connect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.chalmers.lanchat.AdminMessage;
import edu.chalmers.lanchat.ChatActivity;
import edu.chalmers.lanchat.Message;
import edu.chalmers.lanchat.R;

/**
 * Handles a device's detail view and initiates a connection upon request from the user.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {
	private static boolean server_running = false;

	private View contentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.device_detail, null);

        // Tries to connect to the device when the user clicks the connect button.
		contentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                // Show a progress dialog while waiting for the connection.
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
                        //                        new DialogInterface.OnCancelListener() {
                        //
                        //                            @Override
                        //                            public void onCancel(DialogInterface dialog) {
                        //                                ((DeviceActionListener) getActivity()).cancelDisconnect();
                        //                            }
                        //                        }
                );

                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);

            }
        });

        // Disconnect from the group.
		contentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
					}
				});

		return contentView;
	}

    /**
     * Called when the ChatActivity is finished. Used to stop the chat server service.
     * @param requestCode ignored
     * @param resultCode ignored
     * @param data ignored
     */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Stop server
        getActivity().stopService(new Intent(getActivity(), ServerService.class));
        server_running = false;
        // Disconnect from group.
        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (!info.groupFormed) {
            // Only care about actual connections, not disconnections or such.
            return;
        }

        // Dismiss the progress dialog if one is visible.
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		// Set up some info for the UI.
		TextView view = (TextView) contentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text) + ((info.isGroupOwner) ? getResources().getString(R.string.yes) : getResources().getString(R.string.no)));

		view = (TextView) contentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // Start a server if it's not already running
		if (!server_running){
            Intent serverIntent = new Intent(getActivity(), ServerService.class);
            serverIntent.setAction(ServerService.ACTION_RECEIVE);
            serverIntent.putExtra(ServerService.EXTRAS_ECHO, info.isGroupOwner);
            getActivity().startService(serverIntent);
			server_running = true;


            // Notify group owner of your IP address, unless you are the group owner
            if (!info.isGroupOwner) {
                Message ipMessage = new AdminMessage(AdminMessage.Type.IP_NOTIFICATION, IpUtils.getLocalIPAddress());

                Intent addressIntent = new Intent(getActivity(), MessageService.class);
                addressIntent.setAction(MessageService.ACTION_SEND);
                addressIntent.putExtra(MessageService.EXTRAS_MESSAGE, ipMessage.toJson());
                getActivity().startService(addressIntent);
            }
		}

        // Launch the chat activity for text entry.
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(ChatActivity.EXTRA_GROUP_OWNER, info.isGroupOwner);
        startActivityForResult(chatIntent, 0);
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) contentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) contentView.findViewById(R.id.device_info);
		view.setText(device.toString());
	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		contentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) contentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) contentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) contentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) contentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		this.getView().setVisibility(View.GONE);
	}

}

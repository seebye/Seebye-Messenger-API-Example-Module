package com.example.examplemodul;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seebye.messengerapi.api.Contact;
import com.seebye.messengerapi.api.Message;
import com.seebye.messengerapi.api.MessengerAPI;
import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.ErrorCode;
import com.seebye.messengerapi.api.constants.Extra;
import com.seebye.messengerapi.api.constants.MessageType;
import com.seebye.messengerapi.api.constants.Messenger;
import com.seebye.messengerapi.api.constants.ResponseType;

import java.io.PrintWriter;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
	implements receiver.ResponseCallback, View.OnClickListener, AdapterView.OnItemClickListener
{
	private static final int RESULT_SELECT_IMAGE		= 1000;
	private static final int RESULT_SELECT_AUDIO		= 1001;
	private static final int RESULT_SELECT_VIDEO		= 1002;

	private static final int RESPONSE_ACTION_ACCESS			= 1000;
	private static final int RESPONSE_ACTION_CONTACTS		= 1001;
	private static final int RESPONSE_ACTION_SENDMESSAGE	= 1002;
	private static final int RESPONSE_ACTION_SENDMEDIA		= 1003;
	private static final int RESPONSE_ACTION_LOADMESSAGES	= 1004;
	private static final int RESPONSE_ACTION_COUNTMESSAGES	= 1005;

	private ContactAdapter m_adapter;
	private String m_strIDMessenger = null;
	private EditText m_et = null;

	PrintWriter writer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		m_adapter = new ContactAdapter();

		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.sendimage).setOnClickListener(this);
		findViewById(R.id.sendaudio).setOnClickListener(this);
		findViewById(R.id.sendvideo).setOnClickListener(this);
		findViewById(R.id.loadmessages).setOnClickListener(this);
		findViewById(R.id.coutmessages).setOnClickListener(this);
		m_et = (EditText)findViewById(R.id.text);
		ListView list = ((ListView)findViewById(R.id.contacts));
		list.setAdapter(m_adapter);
		list.setOnItemClickListener(this);

		if(
				/**
				 * should be always true as Seebye Messenger API sends a dummy broadcast on receiving the install broadcast of the module
				 * ()
				 * -> the instantiation of the application class should be enforced
				 */
				MessengerAPI.isSecretAvailable()

				&& !MessengerAPI.isEnabled())
		{
			askForAccess();
		}
		else if(MessengerAPI.isEnabled())
		{
			loadContacts();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		receiver.unregisterAllRequest(this);
	}

	private void askForAccess()
	{
		try
		{
			receiver.registerRequest(
					this
					, MessengerAPI.requestAccess()
								.addRequestActionID(RESPONSE_ACTION_ACCESS)
								.send()
								.getID()
					, this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void selectImage()
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, RESULT_SELECT_IMAGE);
	}

	private void selectAudio()
	{
		Intent soundPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		soundPickerIntent.setType("audio/*");
		startActivityForResult(soundPickerIntent, RESULT_SELECT_AUDIO);
	}

	private void selectVideo()
	{
		Intent soundPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		soundPickerIntent.setType("video/*");
		startActivityForResult(soundPickerIntent, RESULT_SELECT_VIDEO);
	}

	private void loadContacts()
	{
		try
		{
			receiver.registerRequest(
					this
					, MessengerAPI.getContacts(Messenger.WHATSAPP.getFlag())
							.addRequestActionID(RESPONSE_ACTION_CONTACTS)
							.send()
							.getID()
					, this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void sendMessage()
	{
		if(m_strIDMessenger == null)
		{
			Toast.makeText(this, R.string.err_select, Toast.LENGTH_SHORT).show();
		}
		else
		{
			try
			{
				receiver.registerRequest(
						this
						, MessengerAPI.sendMessage(Messenger.WHATSAPP, m_strIDMessenger, MessageType.TEXT, m_et.getText().toString())
								.addRequestActionID(RESPONSE_ACTION_SENDMESSAGE)
								.send()
								.getID()
						, this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void loadMessages()
	{
		if(m_strIDMessenger == null)
		{
			Toast.makeText(this, R.string.err_select, Toast.LENGTH_SHORT).show();
		}
		else
		{
			try
			{
				receiver.registerRequest(
						this
						, MessengerAPI.getLastMessages(Messenger.WHATSAPP, m_strIDMessenger, 10, 0)
								.addRequestActionID(RESPONSE_ACTION_LOADMESSAGES)
								.send()
								.getID()
						, this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void countMessages()
	{
		if(m_strIDMessenger == null)
		{
			Toast.makeText(this, R.string.err_select, Toast.LENGTH_SHORT).show();
		}
		else
		{
			try
			{
				receiver.registerRequest(
						this
						, MessengerAPI.getMessageAmount(Messenger.WHATSAPP, m_strIDMessenger)
								.addRequestActionID(RESPONSE_ACTION_COUNTMESSAGES)
								.send()
								.getID()
						, this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	private void sendMedia(MessageType messageType, String strFileLocation)
	{
		if(m_strIDMessenger == null)
		{
			Toast.makeText(this, R.string.err_select, Toast.LENGTH_SHORT).show();
		}
		else
		{
			try
			{
				receiver.registerRequest(
						this
						, MessengerAPI.sendMessage(Messenger.WHATSAPP, m_strIDMessenger, messageType, strFileLocation)
								.addRequestActionID(RESPONSE_ACTION_SENDMEDIA)
								.send()
								.getID()
						, this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}


	private void showError(Bundle bundle)
	{
		final int nErrorCode = bundle.getInt(Extra.ERROR_CODE.getKey());

		Toast.makeText(MainActivity.this, getString(R.string.err, nErrorCode, ErrorCode.fromOrdinal(nErrorCode).name()), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.send:
				sendMessage();
				break;
			case R.id.sendimage:
				selectImage();
				break;
			case R.id.sendaudio:
				selectAudio();
				break;
			case R.id.sendvideo:
				selectVideo();
				break;
			case R.id.loadmessages:
				loadMessages();
				break;
			case R.id.coutmessages:
				countMessages();
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		m_strIDMessenger = m_adapter.getIDMessenger(position);
		Toast.makeText(this, getString(R.string.selected, m_adapter.getName(position)), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResponseReceived(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras)
	{
		switch(nRequestActionID)
		{
			case RESPONSE_ACTION_ACCESS:
				if(responseType == ResponseType.SUCCESS)
				{
					loadContacts();
				}
				else
				{
					Toast.makeText(MainActivity.this, R.string.access_denied, Toast.LENGTH_SHORT).show();
				}
				break;
			case RESPONSE_ACTION_CONTACTS:
				if(responseType == ResponseType.SUCCESS)
				{
					m_adapter.update(Contact.fromBundle(extras));
				}
				break;
			case RESPONSE_ACTION_SENDMEDIA:
			case RESPONSE_ACTION_SENDMESSAGE:
				if(responseType == ResponseType.SUCCESS)
				{
					Toast.makeText(MainActivity.this, R.string.suc_send, Toast.LENGTH_SHORT).show();
				}
				break;
			case RESPONSE_ACTION_LOADMESSAGES:
				if(responseType == ResponseType.SUCCESS)
				{
					ArrayList<Message> aMessages = Message.fromBundle(extras);
					String strDump = "";
					for(Message msg : aMessages)
					{
						strDump += (strDump.isEmpty() ? "" : "\n") + "[" + msg.getType().name() + "] " + msg.getData();
					}

					Toast.makeText(MainActivity.this, strDump, Toast.LENGTH_LONG).show();
				}
				break;
			case RESPONSE_ACTION_COUNTMESSAGES:
				if(responseType == ResponseType.SUCCESS)
				{
					int nMessages = extras.getInt(Extra.MESSAGES_AMOUNT.getKey());
					Toast.makeText(MainActivity.this, String.valueOf(nMessages), Toast.LENGTH_SHORT).show();
				}
				break;
		}

		if(responseType == ResponseType.ERROR)
		{
			showError(extras);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(data != null && resultCode == RESULT_OK)
		{
			String strLocation = IntentHelper.getPath(this, data.getData());
			MessageType messageType = MessageType.UNKNOWN;

			switch(requestCode)
			{
				case RESULT_SELECT_IMAGE:
					messageType = MessageType.IMAGE;
					break;
				case RESULT_SELECT_AUDIO:
					messageType = MessageType.AUDIO;
					break;
				case RESULT_SELECT_VIDEO:
					messageType = MessageType.VIDEO;
					break;
			}

			sendMedia(messageType, strLocation);
		}
	}

	private class ContactAdapter extends BaseAdapter
	{
		private ArrayList<Contact> m_aContacts = new ArrayList<>();

		public void update(ArrayList<Contact> aContacts)
		{
			m_aContacts = aContacts;
			notifyDataSetChanged();
		}

		public String getIDMessenger(int nPosition)
		{
			return getItem(nPosition).getIDMessenger();
		}
		public String getName(int nPosition)
		{
			return getItem(nPosition).getDisplayname();
		}

		@Override
		public int getCount()
		{
			return m_aContacts.size();
		}

		@Override
		public Contact getItem(int position)
		{
			return m_aContacts.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder vh = null;
			Contact contact = getItem(position);

			if(convertView == null)
			{
				convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.row, null);
				vh = new ViewHolder();

				vh.tvName = (TextView)convertView.findViewById(R.id.name);
				vh.tvIDMessenger = (TextView)convertView.findViewById(R.id.idmessenger);

				convertView.setTag(vh);
			}
			if(vh == null)
			{
				vh = (ViewHolder)convertView.getTag();
			}

			vh.tvName.setText(contact.getDisplayname());
			vh.tvIDMessenger.setText(contact.getIDMessenger());

			return convertView;
		}

		private class ViewHolder
		{
			TextView tvName;
			TextView tvIDMessenger;
		}
	}
}

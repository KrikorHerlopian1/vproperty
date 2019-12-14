package edu.newhaven.krikorherlopian.android.vproperty.chatbot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.newhaven.krikorherlopian.android.vproperty.CommonKt;
import edu.newhaven.krikorherlopian.android.vproperty.R;
import edu.newhaven.krikorherlopian.android.vproperty.activity.SearchActivity;
import edu.newhaven.krikorherlopian.android.vproperty.model.HomeFacts;
import edu.newhaven.krikorherlopian.android.vproperty.model.Property;

public class ChatbotFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private static String TAG = "ChatboFragment";
    StreamPlayer streamPlayer = new StreamPlayer();
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private View root;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private boolean listening = false;
    private MicrophoneInputStream capture;
    private Context mContext;
    private MicrophoneHelper microphoneHelper;

    private Assistant watsonAssistant;
    private SessionResponse watsonAssistantSession;
    private SpeechToText speechService;
    private TextToSpeech textToSpeech;

    private void createServices() {
        try {
            watsonAssistant = new Assistant("2018-11-08", new IamAuthenticator(mContext.getString(R.string.assistant_apikey)));
            watsonAssistant.setServiceUrl(mContext.getString(R.string.assistant_url));

            textToSpeech = new TextToSpeech(new IamAuthenticator((mContext.getString(R.string.TTS_apikey))));
            textToSpeech.setServiceUrl(mContext.getString(R.string.TTS_url));

            speechService = new SpeechToText(new IamAuthenticator(mContext.getString(R.string.STT_apikey)));
            speechService.setServiceUrl(mContext.getString(R.string.STT_url));
        } catch (Exception e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = getLayoutInflater().inflate(R.layout.chat_main, container, false);
        try {
            mContext = root.getContext();
            CommonKt.getFragmentActivityCommunication().hideShowMenuItems(false);
            inputMessage = root.findViewById(R.id.message);
            btnSend = root.findViewById(R.id.btn_send);
            btnRecord = root.findViewById(R.id.btn_record);
            String customFont = "Poppins-Light.ttf";
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), customFont);
            inputMessage.setTypeface(typeface);
            recyclerView = root.findViewById(R.id.recycler_view);

            messageArrayList = new ArrayList<>();
            mAdapter = new ChatAdapter(messageArrayList);
            microphoneHelper = new MicrophoneHelper(getActivity());

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            this.inputMessage.setText("");
            this.initialRequest = true;


            int permission = ContextCompat.checkSelfPermission(root.getContext(),
                    Manifest.permission.RECORD_AUDIO);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission to record denied");
                makeRequest();
            } else {
                Log.i(TAG, "Permission to record was already granted");
            }


            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(root.getContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    Message audioMessage = (Message) messageArrayList.get(position);
                    if (audioMessage != null && !audioMessage.getMessage().isEmpty()) {
                        new SayTask().execute(audioMessage.getMessage());
                    }
                }

                @Override
                public void onLongClick(View view, int position) {
                    recordMessage();

                }
            }));

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkInternetConnection()) {
                        sendMessage();
                    }
                }
            });

            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordMessage();
                }
            });

            createServices();
            sendMessage();

        } catch (Exception e) {
        }
        return root;

    }

    // Speech-to-Text Record Audio permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }

            case MicrophoneHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(root.getContext(), "" + getResources().getString(R.string.permission_audio_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    protected void makeRequest() {
        try {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MicrophoneHelper.REQUEST_PERMISSION);
        } catch (Exception e) {
        }

    }

    // Sending a message to Watson Assistant Service
    private void sendMessage() {
        try {
            final String inputmessage = this.inputMessage.getText().toString().trim();
            if (!this.initialRequest) {
                Message inputMessage = new Message();
                inputMessage.setMessage(inputmessage);
                inputMessage.setId("1");
                messageArrayList.add(inputMessage);
            } else {
                Message inputMessage = new Message();
                inputMessage.setMessage(inputmessage);
                inputMessage.setId("100");
                this.initialRequest = false;
                Toast.makeText(root.getContext(), "" + getResources().getString(R.string.tap_voice), Toast.LENGTH_SHORT).show();
            }

            this.inputMessage.setText("");
            mAdapter.notifyDataSetChanged();

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        if (watsonAssistantSession == null) {
                            ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
                            watsonAssistantSession = call.execute().getResult();
                        }

                        MessageInput input = new MessageInput.Builder()
                                .text(inputmessage)
                                .build();
                        MessageOptions options = new MessageOptions.Builder()
                                .assistantId(mContext.getString(R.string.assistant_id))
                                .input(input)
                                .sessionId(watsonAssistantSession.getSessionId())
                                .build();
                        MessageResponse response = watsonAssistant.message(options).execute().getResult();
                        Log.i(TAG, "run: " + response);
                        final Message outMessage = new Message();
                        if (response != null &&
                                response.getOutput() != null &&
                                !response.getOutput().getGeneric().isEmpty() &&
                                "text".equals(response.getOutput().getGeneric().get(0).responseType())) {
                            outMessage.setMessage(response.getOutput().getGeneric().get(0).text());
                            outMessage.setId("2");
                            if (outMessage.getMessage().trim().startsWith("viewproperty")) {
                                String[] split = outMessage.getMessage().trim().split(("-"));
                                Property property = new Property();
                                HomeFacts homeFacts = new HomeFacts();
                                homeFacts.setHomeType(split[1].trim());
                                property.setHomeFacts(homeFacts);

                                try {
                                    Geocoder geocoder = new Geocoder(getContext());
                                    List<Address> address = geocoder.getFromLocationName(split[3].trim(), 5);
                                    Address location = address.get(0);
                                    location.getLatitude();
                                    location.getLongitude();
                                    property.getAddress().setLatitude(location.getLatitude() + "");
                                    property.getAddress().setLongitude(location.getLongitude() + "");
                                } catch (Exception e) {
                                }
                                Intent intent = new Intent(root.getContext(), SearchActivity.class);
                                intent.putExtra("min", "0");
                                intent.putExtra("max", split[2].trim());
                                intent.putExtra("argPojo", property);
                                startActivity(intent);
                                streamPlayer.playStream(textToSpeech.synthesize(new SynthesizeOptions.Builder()
                                        .text("Here are search results for you")
                                        .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                                        .accept(HttpMediaType.AUDIO_WAV)
                                        .build()).execute().getResult());
                            } else {
                                new SayTask().execute(outMessage.getMessage());
                                messageArrayList.add(outMessage);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    if (mAdapter.getItemCount() > 1) {
                                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                                    }

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
        }
    }

    //Record a message via Watson Speech to Text
    private void recordMessage() {
        try {
            if (listening != true) {
                capture = microphoneHelper.getInputStream(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            speechService.recognizeUsingWebSocket(getRecognizeOptions(capture), new MicrophoneRecognizeDelegate());
                        } catch (Exception e) {
                            showError(e);
                        }
                    }
                }).start();
                listening = true;
                Toast.makeText(root.getContext(), "" + getResources().getString(R.string.listening), Toast.LENGTH_LONG).show();

            } else {
                try {
                    microphoneHelper.closeInputStream();
                    listening = false;
                    Toast.makeText(root.getContext(), "" + getResources().getString(R.string.stopped_listening), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
        }
    }

    /**
     * Check Internet Connection
     *
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            // Check for network connections
            if (isConnected) {
                return true;
            } else {
                Toast.makeText(root.getContext(), "" + getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
        }

        return false;
    }

    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions(InputStream audio) {
        return new RecognizeOptions.Builder()
                .audio(audio)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }

    private void showMicText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputMessage.setText(text);
            }
        });
    }

    private void enableMicButton() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnRecord.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(root.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private class SayTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                streamPlayer.playStream(textToSpeech.synthesize(new SynthesizeOptions.Builder()
                    .text(params[0])
                        .voice(SynthesizeOptions.Voice.EN_US_LISAV3VOICE)
                    .accept(HttpMediaType.AUDIO_WAV)
                        .build()).execute().getResult());
            } catch (Exception e) {
            }
            return "Did synthesize";
        }
    }

    //Watson Speech to Text Methods.
    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                showMicText(text);
            }
        }

        @Override
        public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override
        public void onDisconnected() {
            enableMicButton();
        }
    }

}

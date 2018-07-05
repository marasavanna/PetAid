package com.halcyonmobile.adoption.profile;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halcyonmobile.adoption.R;

import com.halcyonmobile.adoption.chat.ChatModels;
import com.halcyonmobile.adoption.chat.ConversationAdapter;
import com.halcyonmobile.adoption.chat.MessagesActivity;
import com.halcyonmobile.adoption.model.User;


import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class SearchUserActivity extends AppCompatActivity {

    ConversationAdapter conversationAdapter;
    private String userId;
    private DatabaseReference databse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        conversationAdapter = new ConversationAdapter();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databse = FirebaseDatabase.getInstance().getReference();
        RecyclerView foundUsersRV = findViewById(R.id.found_users_recycler_view);
        foundUsersRV.setHasFixedSize(true);
        foundUsersRV.setLayoutManager(new LinearLayoutManager(this));
        foundUsersRV.setAdapter(conversationAdapter);

        AutoCompleteTextView serachConv = findViewById(R.id.searched_user_text);

        serachConv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchConversation(s.toString());


            }
        });


        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SearchUserActivity.class);
    }

    private void searchConversation(final String search) {
        conversationAdapter.clear();
        databse.child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatModels.Conversation> conversations = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModels.Conversation conversation = item.getValue(ChatModels.Conversation.class);
                    if (conversation.getUserReceiverId().equals(userId) || conversation.getUserSenderId().equals(userId)) {
                        conversations.add(conversation);
                    }
                }
                for (final ChatModels.Conversation conversation : conversations) {
                    databse.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                User user = item.getValue(User.class);
                                if (user.username.toLowerCase().contains(search.toLowerCase())) {
                                    if (userId.equals(conversation.getUserReceiverId())) {
                                        if (user.id.equals(conversation.getUserSenderId())) {
                                            searchMessageForUser(conversation, user.image, user.username, user.id);
                                        }
                                    } else {
                                        if (user.id.equals(conversation.getUserReceiverId())) {
                                            searchMessageForUser(conversation, user.image, user.username, user.id);
                                        }
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchMessageForUser(final ChatModels.Conversation conversation, final String imageCorespondent, final String corespondentUsername, final String corespondentId) {
        databse.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModels.Message message = item.getValue(ChatModels.Message.class);
                    if (message.getConversationId().equals(conversation.getId()) && message.getTime() == conversation.getLastMessage()) {
                        String messageText = message.getMessage();
                        if (messageText.length() > 25) {
                            messageText = messageText.substring(0, 25) + "...";
                        } else if (messageText.isEmpty()) {
                            messageText = "Photo";
                        }
                        System.out.println("Item added");
                        conversationAdapter.add(new ConversationAdapter.ViewHolder.Conversation(conversation.getId(),
                                imageCorespondent, corespondentUsername, messageText,
                                getTime(conversation.getLastMessage()), new Function1<String, Unit>() {
                            @Override
                            public Unit invoke(String id) {
                                Intent intent = new Intent(SearchUserActivity.this, MessagesActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("corespondentImage", imageCorespondent);
                                intent.putExtra("corespondentUsername", corespondentUsername);
                                intent.putExtra("corespondentId", corespondentId);
                                startActivity(intent);
                                finish();
                                return null;
                            }
                        }));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getTime(long date) {
        Calendar present = Calendar.getInstance();
        Calendar time = Calendar.getInstance();
        time.setTime(new Date(date));
        int days = present.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR);
        if (days > 6) {
            return time.get(Calendar.DAY_OF_MONTH) + " " + getMonthName(time.get(Calendar.MONTH));
        }
        if (days < 1) {
            return time.get(Calendar.HOUR_OF_DAY) + ":" + fixMinute(time.get(Calendar.MINUTE));
        }
        return getDayName(time.get(Calendar.DAY_OF_WEEK));
    }

    private String fixMinute(int minutes) {
        String res = String.valueOf(minutes);
        if (minutes < 10) {
            res = "0" + res;
        }
        return res;
    }

    private String getMonthName(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return "Jan";
            case Calendar.FEBRUARY:
                return "Feb";
            case Calendar.MARCH:
                return "Mar";
            case Calendar.APRIL:
                return "Apr";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "Aug";
            case Calendar.SEPTEMBER:
                return "Sep";
            case Calendar.OCTOBER:
                return "Oct";
            case Calendar.NOVEMBER:
                return "Nov";
            default:
                return "Dec";
        }
    }

    private String getDayName(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return "Sun";

        }
    }
}

package com.example.kotak.shaban_new_1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import utils.DateUtils;

/**
 * Created by kotak on 18/04/2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private String userName;

    public MessageListAdapter(Context context, List<Message> messageList,String userName) {
        this.userName=userName;
        mContext = context;
        mMessageList = messageList;
    }

    public void removeMessage(int position)
    {

    }

    public Message messageContent(int position)
    {
        return mMessageList.get(position);
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        try {
            Message message = (Message) mMessageList.get(position);
            if (message != null && message.getAuthor() != null) {

              //  Log.d("name:", message.getContent());
                if ((message.getAuthor().getFirstName()+" "+message.getAuthor().getLastName()) .equals(userName)) {
                    // If the current user is the sender of the message

                    return VIEW_TYPE_MESSAGE_SENT;
                } else {
                    // If some other user sent the message
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }
            if (message != null &&  !message.getUsername().equals(userName))
            {

                return  VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }catch (Exception e)
        {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        return VIEW_TYPE_MESSAGE_SENT;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText,messageStatus;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            messageStatus=(TextView) itemView.findViewById(R.id.text_message_status);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());

            // Format the stored timestamp into a readable String using method.

            timeText.setText(DateClass.changeDateFormat(message.getCreatedAt()));
            messageStatus.setText(message.getMessageStatus());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        //ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            //profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());
            // Format the stored timestamp into a readable String using method.
         //   Log.d("receiveTime:",message.getCreatedAt());
            timeText.setText(DateClass.changeDateFormat(message.getCreatedAt()));
            if(message.getAuthor()!=null)
                nameText.setText(message.getAuthor().getFirstName()+" "+message.getAuthor().getLastName());
            else if (message.getUsername()!=null)
                nameText.setText(message.getUsername());
        }
    }
}
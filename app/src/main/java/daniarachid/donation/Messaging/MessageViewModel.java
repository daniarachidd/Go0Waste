package daniarachid.donation.Messaging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import daniarachid.donation.Entity.MessageModel;

public class MessageViewModel extends ViewModel implements MessageRepository.OnMessageAdded {

    MutableLiveData<List<MessageModel>> mutableLiveData = new MutableLiveData<>();
    MessageRepository repo = new MessageRepository(this);


    public MessageViewModel() {}

    public void getMessageFromFirestore (String receiverId) {

        repo.getAllMessages(receiverId);
    }
    public void deleteMessageFromFireStore(String receiverId) {

        repo.deleteAllMessages(receiverId);
    }

    public void resetAll() {
        mutableLiveData.postValue(null);
    }

    public LiveData<List<MessageModel>> returnMessages () {
        return mutableLiveData;
    }

    @Override
    public void messagesFromFirestore(List<MessageModel> messageModels) {
        mutableLiveData.setValue(messageModels);
    }
}

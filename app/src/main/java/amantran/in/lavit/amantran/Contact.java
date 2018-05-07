package amantran.in.lavit.amantran;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 2/25/18.
 */

public class Contact implements Parcelable{
    public  String id;
    public String name;
    public String photoURI;
    public String phoneNo;

    public Contact(String id,String name,String photoURI,String phoneNo){
        this.id = id;
        this.name =name;
        this.photoURI =photoURI;
        this.phoneNo = phoneNo;
    }

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        photoURI = in.readString();
        phoneNo = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(photoURI);
        dest.writeString(phoneNo);
    }
}

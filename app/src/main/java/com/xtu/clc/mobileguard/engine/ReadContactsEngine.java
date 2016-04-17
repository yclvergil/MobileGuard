package com.xtu.clc.mobileguard.engine;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.xtu.clc.mobileguard.bean.ContentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clc on 2016/4/17.
 */
public class ReadContactsEngine {
    /**
     * 读取手机联系人
     */
    public static List<ContentBean> readContacts(Context context) {
        List<ContentBean> datas = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        while (cursor.moveToNext()) {
            ContentBean bean = new ContentBean();
            String id = cursor.getString(0);
            Cursor cursor2 = context.getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    new String[]{id}, null);
            while (cursor2.moveToNext()) {
                String name = cursor2.getString(cursor2.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor2.getString(cursor2.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                ));
                bean.setName(name);
                bean.setPhone(number);
            }
            cursor2.close();
            datas.add(bean);
        }
        cursor.close();
        return datas;
    }
}

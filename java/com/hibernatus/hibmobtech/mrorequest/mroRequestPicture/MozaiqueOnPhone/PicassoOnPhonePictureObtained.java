package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

import java.util.Vector;

/**
 * Created by Eric on 20/02/2017.
 */

public interface PicassoOnPhonePictureObtained {

    void onComplete( Vector<PicassoPhoneAlbum> albums );
    void onError();

}

package com.tipbox.app.interfce

import com.tipbox.app.frag.*

interface FragementImp :
    ChangePassword.OnFragmentInteractionListener,
    Informatiom.OnFragmentInteractionListener,
    Notification.OnFragmentInteractionListener,
    PaymentHistory.OnFragmentInteractionListener,
    BankAccount.OnFragmentInteractionListener,
    Profile.OnFragmentInteractionListener,
    QrCode.OnFragmentInteractionListener,
    QrcodeMine.OnFragmentInteractionListener,
    ReceivingTipDisplay.OnFragmentInteractionListener,
    ReceivingTipSet.OnFragmentInteractionListener,
    Setting.OnFragmentInteractionListener,
    StreetQrCode.OnFragmentInteractionListener {


}
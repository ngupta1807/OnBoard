package com.sample.app.intrface

import com.sample.app.frag.BadWordList
import com.sample.app.frag.HashTag
import com.sample.app.frag.TweetList

interface FragementImp : TweetList.OnFragmentInteractionListener, HashTag.OnFragmentInteractionListener
    ,BadWordList.OnFragmentInteractionListener{
}
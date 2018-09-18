package com.iis.fm.service;

import org.springframework.stereotype.Service;

import com.iis.fm.domain.Friends;  //sample
import com.iis.fm.hibernate.dao.FriendsDAO; //sample

@Service
public class FriendService extends BaseService<Friends, FriendDAO> {

}



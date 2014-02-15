package com.example.talksafe.test;

import java.net.ConnectException;

import junit.framework.TestCase;
import android.util.Log;
import apicollection.Member;
import apicollection.UserHandler;

public class APITest extends TestCase {
	
	UserHandler handler;
	
	@Override
	public void setUp(){
		handler = new UserHandler();
		try{
			handler.delete(Member.phoneNumberToHash("6456"));
			handler.delete(Member.phoneNumberToHash("24255"));
			handler.delete(Member.phoneNumberToHash("24256"));
		}catch(Exception e){}
	}
	
	public void testGetAndAdd(){
		
		try{
			Member mem1 = new Member("6456","123", 364);
			mem1.phoneNumberToHash();
			assertTrue(handler.add(mem1));
			Member mem = handler.get(Member.phoneNumberToHash("6456"));
			assertEquals(mem, mem1);
		}catch(ConnectException e){
			assertTrue(false);
		}catch(Exception e){
			assertTrue(false);
		}
		
	}
	
	public void testDelete(){
		
		try{
			Member mem = new Member("24255","192",5060);
			mem.phoneNumberToHash();
			handler.add(mem);
			assertTrue(handler.delete(mem.getPhone()));
			assertFalse(handler.delete("a"));
		}catch(ConnectException e){
			assertTrue(false);
		}catch(Exception e){
			assertTrue(false);
		}
		
	}
	
	public void testEdit(){
		
		try{
			Member mem = new Member("24256","192",5060);
			mem.phoneNumberToHash();
			Member mem2 = mem.clone();
			mem2.setIPAdress("168");
			assertTrue(handler.add(mem));
			assertTrue(handler.change(mem2));
			assertEquals(mem2,handler.get(mem2.getPhone()));
		}catch(ConnectException e){
			assertFalse(true);
		}catch(Exception e){
			assertTrue(false);
		}
		
	}
	
}

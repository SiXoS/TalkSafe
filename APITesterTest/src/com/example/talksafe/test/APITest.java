package com.example.talksafe.test;

import java.net.ConnectException;

import junit.framework.TestCase;
import apicollection.Member;
import apicollection.UserHandler;

public class APITest extends TestCase {
	
	public void testGet(){
		
		UserHandler handler = new UserHandler();
		try{
			Member mem = handler.get(Member.phoneNumberToHash("2474"));
			assertTrue(mem.getPhone().equals(Member.phoneNumberToHash("2474")));
		}catch(ConnectException e){
			assertTrue(false);
		}catch(Exception e){
			assertTrue(false);
		}
		
	}
	
	public void testAdd(){
		
		UserHandler handler = new UserHandler();
		try {
			Member mem = new Member("2474","192",5060);
			mem.phoneNumberToHash();
			assertTrue(handler.add(mem));
			Member mem2 = handler.get(mem.getPhone());
			assertEquals(mem,mem2);
			
		} catch (ConnectException e) {
			assertTrue(false);
		}catch(Exception e){
			assertTrue(false);
		}
		
	}
	
	public void testDelete(){
		
		UserHandler handler = new UserHandler();
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
		
		UserHandler handler = new UserHandler();
		try{
			Member mem = new Member("24255","192",5060);
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

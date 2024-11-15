package com.busbooking;

class UserContext {
	
	private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
        System.out.println("Success");
        System.out.println(user);
    }

    public static User getUser() {
    	System.out.println("user con =" + userThreadLocal.get());
        return userThreadLocal.get();
    }

    public static void clear() {
        userThreadLocal.remove();
    }
    
}
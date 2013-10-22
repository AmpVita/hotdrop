package com.gethotdrop.android.core;

import java.util.Date;

public class User {
        private int id;
        
        public User(int id) {
                this.id = id;
        }
        
        public int getId() {
                return id;
        }
        
        public boolean equals(Object o) {
                if ((o instanceof User) && this.id == ((User)o).id)
                        return true;
                
                return false;
        }
}
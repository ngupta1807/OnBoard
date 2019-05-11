package com.grabid.models;

import java.util.ArrayList;

/**
 * Created by vinod on 10/25/2016.
 */
public class Items {

        private String pSubCatName;
        private ArrayList<ItemList> mItemListArray;
        private boolean isChecked;;
        private boolean isVisible;;
        String id;

        public Items(String id, String pSubCatName,
                           ArrayList<ItemList> mItemListArray, boolean isChecked)
        {
            super();
            this.id = id;
            this.pSubCatName = pSubCatName;
            this.mItemListArray = mItemListArray;
            this.isChecked = isChecked;
        }
        public Items(String id, String pSubCatName,
                           ArrayList<ItemList> mItemListArray, boolean isChecked, boolean isVisible)
        {
            super();
            this.id = id;
            this.pSubCatName = pSubCatName;
            this.mItemListArray = mItemListArray;
            this.isChecked = isChecked;
            this.isVisible = isVisible;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getCatName()
        {
            return pSubCatName;
        }

        public void setCatName(String pSubCatName)
        {
            this.pSubCatName = pSubCatName;
        }

        public ArrayList<ItemList> getmItemListArray()
        {
            return mItemListArray;
        }

        public void setmItemListArray(ArrayList<ItemList> mItemListArray)
        {
            this.mItemListArray = mItemListArray;
        }

        public boolean isChecked()
        {
            return isChecked;
        }
        public boolean isVisible()
        {
            return isVisible;
        }

        public void setChecked(boolean isChecked)
        {
            this.isChecked = isChecked;
        }
        public void setVisible(boolean isVisible)
        {
            this.isVisible = isVisible;
        }

        /**
         *
         * second level item
         *
         */
        public static class ItemList {
            private String itemName;
            private boolean isChecked;
            private String id;

            public ItemList(String id, String itemName, boolean isChecked) {
                super();
                this.itemName = itemName;
                this.isChecked = isChecked;
                this.id = id;
            }

            public String getItemName() {
                return itemName;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setItemName(String itemName) {
                this.itemName = itemName;
            }

            public boolean isChecked() {
                return isChecked;
            }

            public void setChecked(boolean isChecked) {
                this.isChecked = isChecked;
            }
        }
}

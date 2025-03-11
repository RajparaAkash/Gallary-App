package com.gallaryapp.privacyvault.photoeditor.Model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public class Querys {
    public String[] args;
    public boolean ascending;
    public int limit;
    public String[] projection;
    public String selection;
    public String sort;
    public Uri uri;

    Querys(Builder builder) {
        this.uri = builder.uri;
        this.projection = builder.projection;
        this.selection = builder.selection;
        this.args = builder.getStringArgs();
        this.sort = builder.sort;
        this.ascending = builder.ascending;
        this.limit = builder.limit;
    }

    public Cursor getCursor(ContentResolver cr) {
        return cr.query(this.uri, this.projection, this.selection, this.args, hack());
    }

    private String hack() {
        if (this.sort == null && this.limit == -1) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String str = this.sort;
        if (str != null) {
            builder.append(str);
        } else {
            builder.append(1);
        }
        builder.append(" ");
        if (!this.ascending) {
            builder.append("DESC");
            builder.append(" ");
        }
        if (this.limit != -1) {
            builder.append("LIMIT");
            builder.append(" ");
            builder.append(this.limit);
        }
        return builder.toString();
    }

    public String toString() {
        return "Query{\nuri=" + this.uri + "\nprojection=" + Arrays.toString(this.projection) + "\nselection='" + this.selection + "'\nargs=" + Arrays.toString(this.args) + "\nsortMode='" + this.sort + "'\nascending='" + this.ascending + "'\nlimit='" + this.limit + "'}";
    }


    public static final class Builder {
        Uri uri = null;
        String[] projection = null;
        String selection = null;
        Object[] args = null;
        String sort = null;
        int limit = -1;
        boolean ascending = false;

        public Builder uri(Uri val) {
            this.uri = val;
            return this;
        }

        public Builder projection(String[] val) {
            this.projection = val;
            return this;
        }

        public Builder selection(String val) {
            this.selection = val;
            return this;
        }

        public Builder args(Object... val) {
            this.args = val;
            return this;
        }

        public Builder sort(String val) {
            this.sort = val;
            return this;
        }

        public Builder ascending(boolean val) {
            this.ascending = val;
            return this;
        }

        public Querys build() {
            return new Querys(this);
        }

        public String[] getStringArgs() {
            if (Build.VERSION.SDK_INT >= 24) {
                return (String[]) Arrays.stream(this.args).map(new Function() {
                    @Override
                    public Object apply(Object obj) {
                        return obj.toString();
                    }
                }).toArray(new IntFunction() {
                    @Override
                    public Object apply(int i) {
                        return Builder.getStringArgs0(i);
                    }
                });
            }
            String[] list = new String[this.args.length];
            int i = 0;
            while (true) {
                Object[] objArr = this.args;
                if (i >= objArr.length) {
                    return list;
                }
                list[i] = String.valueOf(objArr[i]);
                i++;
            }
        }


        public static String[] getStringArgs0(int x0) {
            return new String[x0];
        }
    }
}

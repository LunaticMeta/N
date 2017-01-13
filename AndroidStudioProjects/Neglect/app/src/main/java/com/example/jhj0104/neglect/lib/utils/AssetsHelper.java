package com.example.jhj0104.neglect.lib.utils;

/**
 * Created by jhj0104 on 2017-01-06.
 */
/*
 * libcommon
 * utility/helper classes for myself
 *
 * Copyright (c) 2014-2016 saki t_saki@serenegiant.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AssetsHelper {
    public AssetsHelper() {
    }


    public static String loadString(AssetManager assets, String name) throws IOException {
        StringBuffer sb = new StringBuffer();
        char[] buf = new char[1024];
        BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open(name)));
        int r = reader.read(buf);

        while(r > 0) {
            sb.append(buf, 0, r);
        }

        return sb.toString();
    }
}

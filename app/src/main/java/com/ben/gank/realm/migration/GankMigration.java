/*
 *    Copyright 2015 TedXiong <xiong-wei@hotmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ben.gank.realm.migration;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by Ted on 2015/8/27.
 */
public class GankMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        if(oldVersion==0){
            oldVersion++;
        }
    }
}

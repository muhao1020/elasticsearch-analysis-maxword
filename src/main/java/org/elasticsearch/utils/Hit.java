/**
 * 
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 */
package org.elasticsearch.utils;

/**
 * 表示一次词典匹配的命中
 */
public class Hit {
	//Hit不匹配 00
	public static final int UNMATCH = 0;
	//Hit完全匹配  01
	public static final int MATCH = 1;
	//Hit前缀匹配  10
	public static final int PREFIX = 1 << 1;

	
	//该HIT当前状态，默认未匹配，
	// 注意的的是 hitState = value << 2 |  hitState; value = hitState >> 2
	private int hitState;
	
	public Hit() {
		hitState = UNMATCH;
	}
	public Hit(int state) {
		this.hitState = state;
	}

	public void setHitState(int state) {
		this.hitState = this.hitState | state;
	}

	public int getHitState() {
		return hitState;
	}

	public void setValue(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("value must greater than zero");
		} else if (value > 1 << 30) {
			// todo 数据范围需要再次确认
			throw new IllegalArgumentException("value must less than zero");
		}
		hitState = value << 2 |  hitState;
	}

	public int getValue() {
		return hitState >> 2;
	}

	
	/**
	 * 判断是否完全匹配
	 */
	public boolean isMatch() {
		return (this.hitState & MATCH) > 0;
	}

	/**
	 * 判断是否是词的前缀
	 */
	public boolean isPrefix() {
		return (this.hitState & PREFIX) > 0;
	}
	/**
	 * 判断是否是不匹配
	 */
	public boolean isUnmatch() {
		return (this.hitState | UNMATCH) == UNMATCH ;
	}

}

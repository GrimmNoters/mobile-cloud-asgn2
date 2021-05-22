/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Controller
public class HTTPController {
	@Autowired
	private VideoRepository videoRepository;

//	@GET(VIDEO_SVC_PATH)
//	public Collection<Video> getVideoList();
//	Returns the list of videos that have been added to the server as JSON.
//	The list of videos should be persisted using Spring Data.
//	The list of Video objects should be able to be unmarshalled by the client into a Collection
//	The return content-type should be application/json, which will be the default if you use @ResponseBody
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH,method=RequestMethod.GET)
	public @ResponseBody
	Collection<Video> getAll(){
		Collection<Video> list = new ArrayList<Video>();
		Iterable<Video> tmp = videoRepository.findAll();
		for(Video v : tmp){
			list.add(v);
		}
		return list;
	}


//	@GET(VIDEO_SVC_PATH + "/{id}")
//	public Video getVideoById(@Path("id") long id);
//
//	Returns the video with the given id or 404 if the video is not found.
	@RequestMapping(value =  VideoSvcApi.VIDEO_SVC_PATH+"/{id}", method =  RequestMethod.GET)
	public @ResponseBody
	Video getVideoByID(@PathVariable long id){
		return videoRepository.findByid(id);
	}


//	@POST(VIDEO_SVC_PATH)
//	public Video addVideo(@Body Video v);
//
//	The video metadata is provided as an application/json request body.
//	The JSON should generate a valid instance of the Video class when deserialized by Spring's default Jackson library.
//	Returns the JSON representation of the Video object that was stored along with any updates to that object made by the server.
//	The server should store the Video in a Spring Data JPA repository. If done properly, the repository should handle generating ID's.
//	A video should not have any likes when it is initially created.
//	You will need to add one or more annotations to the Video object in order for it to be persisted with JPA.
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH,method =  RequestMethod.POST)
	public @ResponseBody
	Video addVideo(@RequestBody Video v){
		return videoRepository.save(v);
	}

//	@POST(VIDEO_SVC_PATH + "/{id}/like")
//	public Void likeVideo(@Path("id") long id);
//
//	Allows a user to like a video. Returns 200 Ok on success, 404 if the video is not found, or 400 if the user has already liked the video.
//	The service should keep track of which users have liked a video and prevent a user from liking a video twice.
//	A POJO Video object is provided for you and you will need to annotate and/or add to it in order to make it persistable.
//	A user is only allowed to like a video once. If a user tries to like a video a second time, the operation should fail and return 400 Bad Request.
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH+"/{id}/like", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<HttpStatus> likeVideo(@PathVariable long id, Principal user){
		Video v = videoRepository.findByid(id);
		if(v == null){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		else {
			if(v.getLikedBy().contains(user.getName())){
				return new ResponseEntity(HttpStatus.BAD_REQUEST);
			}
			else {
				v.setLikes(v.getLikes()+1);
				Set<String> likeSets = v.getLikedBy();
				likeSets.add(user.getName());
				v.setLikedBy(likeSets);
				videoRepository.save(v);
				return new ResponseEntity(HttpStatus.ACCEPTED);
			}
		}
	}


//	@POST(VIDEO_SVC_PATH + "/{id}/unlike")
//	public Void unlikeVideo(@Path("id") long id);
//
//	Allows a user to unlike a video that he/she previously liked.
//	Returns 200 OK on success, 404 if the video is not found, and a 400 if the user has not previously liked the specified video.
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH+"/{id}/unlike", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity unlikeVideo(@PathVariable long id, Principal user) {
		Video v = videoRepository.findByid(id);
		if (v == null) {
			return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
		else {
			System.out.println(v.getLikes());
			if(v.getLikes() == 0){
				return new ResponseEntity(HttpStatus.BAD_REQUEST);
			}
			if(!v.getLikedBy().contains(user.getName())){
				return new ResponseEntity(HttpStatus.BAD_REQUEST);
			}
			else {
				v.setLikes(v.getLikes()-1);
				System.out.println(v.getLikes());
				Set<String> likeSets = v.getLikedBy();
				likeSets.remove(user.getName());
				v.setLikedBy(likeSets);
				videoRepository.save(v);
				return new ResponseEntity(HttpStatus.ACCEPTED);
			}
		}
	}

//	@GET(VIDEO_TITLE_SEARCH_PATH)
//	public Collection<Video> findByTitle(@Query(TITLE_PARAMETER) String title);
//
//	Returns a list of videos whose titles match the given parameter or an empty list if none are found.
	@RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody
	Collection<Video> findByTitle(@RequestParam String title){
		return videoRepository.findByName(title);
	}



//	@GET(VIDEO_DURATION_SEARCH_PATH)
//	public Collection<Video> findByDurationLessThan(@Query(DURATION_PARAMETER) long duration);
//
//	Returns a list of videos whose durations are less than the given parameter or an empty list if none are found.
	@RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody
	Collection<Video> findByDurationLessThan(@RequestParam long duration){
		return videoRepository.findByDurationLessThan(duration);
	}
}

package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long>{

    Collection<Video> findByName(
            @Param(VideoSvcApi.TITLE_PARAMETER) String title);
    Collection<Video> findByDurationLessThan(
            @Param(VideoSvcApi.DURATION_PARAMETER) long maxDuration);
    Video findByid(long id);
}

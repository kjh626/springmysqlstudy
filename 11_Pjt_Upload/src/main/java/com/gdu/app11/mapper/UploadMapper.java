package com.gdu.app11.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.app11.domain.AttachDTO;
import com.gdu.app11.domain.UploadDTO;

@Mapper
public interface UploadMapper {
	// 통상 서비스의 개수가 매퍼의 개수보다 적을 수밖에 없고, 어디에 뭐가 쓰이는 지도 헷갈린다. 필요하면 서비스별로 적어서 구분해두면 좋다.
	
	// getUploadList
	public int getUploadCount();
	public List<UploadDTO> getUploadList(Map<String, Object> map);
	
	// addUpload
	public int addUpload(UploadDTO uploadDTO);
	public int addAttach(AttachDTO attachDTO);
	
	// getUploadByNo 
	public UploadDTO getUploadByNo(int uploadNo);
	public List<AttachDTO> getAttachList(int uploadNo);
	
	// display, download
	public AttachDTO getAttachByNo(int attachNo);
	
	// download
	public int increaseDownloadCount(int attachNo);
	
	// downloadAll
	// public List<AttachDTO> getAttachList(int uploadNo);
	
	// removeUpload
	public int removeUpload(int uploadNo);
	
	// modifyUpload
	public int modifyUpload(UploadDTO uploadDTO);
	
	// removeAttach
	public int removeAttach(int attachNo);
	
	// RemoveWrongfileScheduler
	public List<AttachDTO> getAttachListInYesterday();

}
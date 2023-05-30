package com.gdu.app11.batch;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gdu.app11.domain.AttachDTO;
import com.gdu.app11.mapper.UploadMapper;
import com.gdu.app11.util.MyFileUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor    // field의 @Autowired 처리
@EnableScheduling
@Component
public class RemoveWrongfileScheduler {

	// field
	private MyFileUtil myFileUtil;
	private UploadMapper uploadMapper;
	
	@Scheduled(cron="0 54 17 1/1 * ?")  // www.cronmaker.com에서 생성한 매일 새벽 3시 정보에서 마지막 필드 *를 지워줌
	public void execute() {
		
		// 어제 업로드 된 첨부 파일들의 정보(DB에서 가져오기)
		List<AttachDTO> attachList = uploadMapper.getAttachListInYesterday(); 
		
		// List<AttachDTO> -> list<Path>로 변환하기 (Path : 경로 + 파일명)
		List<Path> pathList = new ArrayList<Path>();
		if(attachList != null && attachList.isEmpty() == false) {  // 첨부된 게 있으면
			for(AttachDTO attachDTO : attachList) {
				pathList.add(new File(attachDTO.getPath(), attachDTO.getFilesystemName()).toPath());  // Path 만들기 : File객체.toPath()
				if(attachDTO.getHasThumbnail() == 1) {
					pathList.add(new File(attachDTO.getPath(), "s_" + attachDTO.getFilesystemName()).toPath());  // Path 만들기 : File객체.toPath()
				}
			}
		
		}
	
		// 어제 업로드된 경로
		String yesterdayPath = myFileUtil.getYesterdayPath();
		
		// 어제 업로드된 파일 목록(HDD에서 확인) 중에서 DB에는 정보가 없는 파일 목록
		File dir = new File(yesterdayPath);
		// 파일 이름가지고 골라가지고 계산해서 다 가져오는 게 있다. listFiles(FilenameFilter filter) -> 인터페이스를 new하는 건 불가능한데, 인터페이스가 가지고 있는 메소드(추상메소드)를 구현한다면 new 가능해진다!
		// Java Stream 보충에서 배울 수 있음.
		File[] wrongFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {  // true를 반환하면 File[] wrongFiles에 포함된다. false를 반환하면 포함이 안 된다.  매개변수 File dir, String name은 HDD에 저장된 파일을 의미한다.
				// DB에 있는 목록 : pathList                - 이미 Path
				// HDD에 있는 파일 : File dir, String name  - File.toPath() 처리해서 Path로 변경
				return pathList.contains(new File(dir, name).toPath()) == false;  // HDD에 있는 파일이 path에 포함될 때 false로 반환. 하면 잘못된 파일목록이 wrongFiles에 저장..?
			}
		});
		
		// File[] wrongFiles 모두 삭제
		if(wrongFiles != null && wrongFiles.length != 0) {  // 배열이라서 체크법이 조금 다름
			for(File wrongFile : wrongFiles) {
				wrongFile.delete();
			}
		}
	}
	
}

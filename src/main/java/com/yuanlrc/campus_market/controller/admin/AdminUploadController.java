package com.yuanlrc.campus_market.controller.admin;

import com.yuanlrc.campus_market.bean.CodeMsg;
import com.yuanlrc.campus_market.bean.Result;
import com.yuanlrc.campus_market.util.PathUtil;
import com.yuanlrc.campus_market.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 后台公用上传控制器
 */
@RequestMapping("/admin/upload")
@Controller
public class AdminUploadController {

	@Value("${ylrc.upload.photo.sufix}")
	private String uploadPhotoSufix;

	@Value("${ylrc.upload.photo.maxsize}")
	private long uploadPhotoMaxSize;

	private Logger log = LoggerFactory.getLogger(AdminUploadController.class);

	/**
	 * 图片统一上传
	 */
	@RequestMapping(value = "/upload_photo", method = RequestMethod.POST)
	@ResponseBody
	public Result<String> uploadPhoto(@RequestParam(name = "photo", required = true) MultipartFile photo) {
		String originalFilename = photo.getOriginalFilename();
		String suffix = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
		if (!uploadPhotoSufix.contains(suffix.toLowerCase())) {
			return Result.error(CodeMsg.UPLOAD_PHOTO_SUFFIX_ERROR);
		}
		if (photo.getSize() / 1024 > uploadPhotoMaxSize) {
			CodeMsg codeMsg = CodeMsg.UPLOAD_PHOTO_ERROR;
			codeMsg.setMsg("图片大小不能超过" + (uploadPhotoMaxSize / 1024) + "M");
			return Result.error(codeMsg);
		}
		String uploadPhotoPath = PathUtil.newInstance().getUploadPhotoPath();
		File filePath = new File(uploadPhotoPath);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		filePath = new File(uploadPhotoPath + "/" + StringUtil.getFormatterDate(new Date(), "yyyyMMdd"));
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		String filename = StringUtil.getFormatterDate(new Date(), "yyyyMMdd") + "/" + System.currentTimeMillis() + suffix;
		try {
			photo.transferTo(new File(uploadPhotoPath + "/" + filename));
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return Result.error(CodeMsg.UPLOAD_PHOTO_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			return Result.error(CodeMsg.UPLOAD_PHOTO_ERROR);
		}
		log.info("图片上传成功，保存位置：" + uploadPhotoPath + filename);
		return Result.success(filename);
	}
}

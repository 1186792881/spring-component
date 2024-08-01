package com.wangyi.component.plugin.main.controller;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.upload.UploadParam;
import com.gitee.starblues.integration.user.PluginUser;
import com.wangyi.component.plugin.main.plugin.HelloInterface;
import com.wangyi.component.plugin.main.service.MainService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class MainController {

    @Resource
    private PluginOperator pluginOperator;

    @Resource
    private PluginUser pluginUser;

    @Resource
    private MainService mainService;

    @GetMapping("/main/hello")
    public String mainHello() {
        return mainService.hello();
    }

    @GetMapping("/plugin/hello")
    public String pluginHello(String pluginId) {
        // 获取指定插件中的实现类
       List<HelloInterface> helloInterfaces = pluginUser.getBeanByInterface(pluginId, HelloInterface.class);
       return helloInterfaces.get(0).hello();
    }

    /**
     * 获取插件信息
     *
     * @return 返回插件信息
     */
    @GetMapping("/getPluginInfo")
    public List<PluginInfo> getPluginInfo() {
        return pluginOperator.getPluginInfo();
    }

    /**
     * 校验插件包
     * @param pluginPath 插件路径
     * @return 返回操作结果
     */
    @PostMapping("/verify")
    public String verify(@RequestParam("pluginPath") String pluginPath){
        try {
            if(pluginOperator.verify(Paths.get(pluginPath))){
                return "verify success";
            } else {
                return "verify failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "verify failure." + e.getMessage();
        }
    }


    /**
     * 加载插件包
     * @param pluginPath 插件路径
     * @return 返回操作结果
     */
    @PostMapping("/load")
    public PluginInfo load(@RequestParam("pluginPath") String pluginPath,
                           @RequestParam(value = "unpackPlugin", required = false) Boolean unpackPlugin){
        try {
            if(unpackPlugin == null){
                unpackPlugin = false;
            }
            return pluginOperator.load(Paths.get(pluginPath), unpackPlugin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解析插件包
     * @param pluginPath 插件路径
     * @return 返回操作结果
     */
    @PostMapping("/parse")
    public PluginInfo parse(@RequestParam("pluginPath") String pluginPath){
        try {
            return pluginOperator.parse(Paths.get(pluginPath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据插件id停止插件
     * @param id 插件id
     * @return 返回操作结果
     */
    @PostMapping("/stop/{id}")
    public String stop(@PathVariable("id") String id){
        try {
            if(pluginOperator.stop(id)){
                return "plugin '" + id +"' stop success";
            } else {
                return "plugin '" + id +"' stop failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "plugin '" + id +"' stop failure. " + e.getMessage();
        }
    }

    /**
     * 根据插件id启动插件
     * @param id 插件id
     * @return 返回操作结果
     */
    @PostMapping("/start/{id}")
    public String start(@PathVariable("id") String id){
        try {
            if(pluginOperator.start(id)){
                return "plugin '" + id +"' start success";
            } else {
                return "plugin '" + id +"' start failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "plugin '" + id +"' start failure. " + e.getMessage();
        }
    }


    /**
     * 根据插件id卸载插件
     * @param id 插件id
     * @return 返回操作结果
     */
    @PostMapping("/uninstall/{id}")
    public String uninstall(@PathVariable("id") String id){
        try {
            pluginOperator.uninstall(id, true, true);
            return "plugin '" + id +"' uninstall success";
        } catch (Exception e) {
            e.printStackTrace();
            return "plugin '" + id +"' uninstall failure. " + e.getMessage();
        }
    }


    /**
     * 根据插件路径安装插件。该插件jar必须在服务器上存在。注意: 该操作只适用于生产环境
     * @param path 插件路径名称
     * @return 操作结果
     */
    @PostMapping("/installByPath")
    public String install(@RequestParam("path") String path,
                          @RequestParam(value = "unpackPlugin", defaultValue = "false", required = false) Boolean unpackPlugin){
        try {
            PluginInfo pluginInfo = pluginOperator.install(Paths.get(path), unpackPlugin);
            if(pluginInfo != null){
                return "installByPath success";
            } else {
                return "installByPath failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "installByPath failure : " + e.getMessage();
        }
    }


    /**
     * 上传并安装插件。注意: 该操作只适用于生产环境
     * @param multipartFile 上传文件 multipartFile
     * @return 操作结果
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("jarFile") MultipartFile multipartFile){
        try {
            UploadParam uploadParam = UploadParam.byMultipartFile(multipartFile)
                    .setBackOldPlugin(true)
                    .setStartPlugin(true)
                    .setUnpackPlugin(false);
            PluginInfo pluginInfo = pluginOperator.uploadPlugin(uploadParam);
            if(pluginInfo != null){
                return "install success";
            } else {
                return "install failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "install failure : " + e.getMessage();
        }
    }

    /**
     * 备份插件。注意: 该操作只适用于生产环境
     * @param pluginId 插件id
     * @return 操作结果
     */
    @PostMapping("/back/{pluginId}")
    public String backupPlugin(@PathVariable("pluginId") String pluginId){
        try {
            Path path = pluginOperator.backupPlugin(pluginId, "testBack");
            if(path != null){
                return "backupPlugin success: " + path;
            } else {
                return "backupPlugin failure";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "backupPlugin failure : " + e.getMessage();
        }
    }
}
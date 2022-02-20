<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-cascades"></i> 新建订单明细
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                 <el-steps :active="active" align-center finish-status="success">
                    <el-step title="上传文件" description="上传文件"></el-step>
                    <el-step title="填写表单" description="填写表单"></el-step>
                    <el-step title="运行" description="运行"></el-step>
                </el-steps>
                <div class="step1" v-if="active === 0">
                    <el-upload
                        class="upload-demo"
                        drag
                        action="http://116.62.155.169:8080/upload"
                        multiple
                    >
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">
                            拖拽文件到这里 或者 <em>单击上传</em>
                        </div>
                    </el-upload>
                </div>
                <div class="step2" v-if="active === 1">
                    <div class="form-box">
                        <el-form ref="formRef" :rules="rules" :model="form" label-width="80px">
                            <el-form-item label="类型：" prop="type">
                                <el-radio-group v-model="form.type">
                                    <el-radio border label="实时"></el-radio>
                                    <el-radio border label="昨日"></el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="step3" v-if="active === 2">
                    <div class="form-box" style="text-align: center">
                        <!-- <div class="demo-progress" style="text-align: center">
                            
                            <el-progress
                            :text-inside="true"
                            :stroke-width="15"
                            :percentage="100"
                            status="success"
                            />
                        </div> -->
                        
                    </div>
                </div>
                <div class="result-table" v-if="executeStatus === 1">
                            <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" max-height="400">
                                <!-- <el-table-column prop="id" label="ID" width="55" align="center"></el-table-column> -->
                                <el-table-column prop="name" label="文件名"></el-table-column>
                                <!-- <el-table-column prop="category" label="类型" width="55"></el-table-column> -->
                                <!-- <el-table-column label="状态" align="center" width="80">
                                    <template #default="scope">
                                        <el-tag :type="
                                                scope.row.state === '成功'
                                                    ? 'success'
                                                    : scope.row.state === '失败'
                                                    ? 'danger'
                                                    : 'info'
                                            ">{{ scope.row.state }}</el-tag>
                                    </template>
                                </el-table-column> -->
                                <el-table-column prop="size" label="文件大小" width="70"></el-table-column>
                                <el-table-column prop="date" label="修改时间" width="200" align="center"></el-table-column>
                                <el-table-column label="操作" width="200" align="center">
                                    <template #default="scope">
                                        <el-button type="text" icon="el-icon-down" @click="handleDownload(scope.$index, scope.row)">下载
                                        </el-button>
                                       
                                    </template>
                                </el-table-column>
                            </el-table>
                        </div>
                
            </div>
            <div class="setpButton" >
                <el-button   type="primary" @click="next" v-if="active===0">下一步</el-button>
                <el-button   type="primary" @click="onSubmit" v-if="active===1">下一步</el-button>
                <el-button   type="primary" @click="execute" v-if="active===2">运行</el-button>
            </div>
            <div class="executeButton" @click="next">
                
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { fetchData } from "../api/index";
import { postData, getDownload } from "../api/index";
import { router } from "../router/index";

export default {
    name: "createOrder",
    data(){
        return {
            active: 0,
            executeStatus: 0,
            tableData: ref([]),
        }
    },
    methods: {
        
        next() {
            if (this.active++ >= 2) this.active = 0;
        },
        onSubmit() {
            if (this.active++ >= 2) this.active = 0;
        },
        execute() {
            let data = new FormData();
            data.append("isYesterdayParam", this.form.type);
           
            console.log("form data", this.form)
           
            console.log("post data:", data.get("isYesterdayParam"));
            let executeStatus = this.executeStatus;
            postData(data).then(res=>{
                if (res.status === 'success') {
                    this.executeStatus = 1;
                    this.active = 3;
                    this.tableData=res.tableData;
                }
                console.log("结果集:", res);
                console.log("结果怎样？",res.status);
            })
        },
        handleDownload(index, row) {
            let params = reactive({
                filename: row.name
            })
            getDownload(params).then(res=>{ 
                console.log(res)               
                const aLink = document.createElement('a');
                const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
                aLink.href = URL.createObjectURL(blob);
                aLink.setAttribute(
                'download',
                `${row.name}`
                ); // 设置下载文件名称
                aLink.click();
                // document.body.appendChild(aLink)
                // this.$refs.loadElement.append(aLink);
                
            });
        },
    },
    setup() {
        const rules = {
            type: [
                { required: true, message: "请选择类型", trigger: "blur" },
            ],
        };
        const formRef = ref(null);
        const form = reactive({
            type: "实时",
        });
        // 重置
        const onReset = () => {
            formRef.value.resetFields();
        };

        return {
            rules,
            formRef,
            form,
            onReset,
        };
    },
};
</script>
<style>
:root {
  --el-color-primary: #0a1e31;
  --el-color-success: #67c23a;
  --el-color-warning: #e6a23c;
  --el-color-danger: #f56c6c;
  --el-color-error: #f56c6c;
  --el-color-info: #909399;
}
</style>
<style scoped>
.demo-progress .el-progress--line {
    margin-left:59%;
    margin-right:60%;
    margin-top: 20px;
  margin-bottom: 15px;
  width: 350px;
  text-align: center;
}
.handle-box {
    margin-bottom: 20px;
}

.handle-select {
    width: 120px;
}

.handle-input {
    width: 300px;
    display: inline-block;
}
.table {
    width: 100%;
    font-size: 12px;
}
.red {
    color: #ff0000;
}
.mr10 {
    margin-right: 10px;
}
.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}
.upload-demo {
    text-align: center;
    padding: 20px;
}
.setpButton {
  padding-left: 46.6%;
  padding-right: 50%;
}
.step2 {
    padding-left: 10%;
    padding-top: 1%;
    padding-bottom: 1%;
    /* text-align: center; */
}
.step3 {
    width: 100%;
    font-size: 12px;
}
</style>


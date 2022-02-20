<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-cascades"></i> 订单明细
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-row>
                    <el-col :span="22">
                        <div>
                        <el-select v-model="query.category" placeholder="类型" class="handle-select mr10">
                            <el-option key="1" label="实时" value="实时"></el-option>
                            <el-option key="2" label="昨日" value="昨日"></el-option>
                        </el-select>
                         <el-input v-model="query.name" placeholder="文件名" class="handle-input mr10"></el-input>   
                         <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button> 
                        </div>
                    </el-col>
                    <!-- <el-col :span="8">
                        <el-input v-model="query.name" placeholder="文件名" class="handle-input mr10"></el-input>        
                    </el-col>
                    <el-col :span="12">
                        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>        
                    </el-col> -->
                    <el-col :span="1" justify="end">
                        <el-button type="primary" @click="handleCreate" justify="end">新建</el-button>
                    </el-col>
                </el-row>

                
                
                
            </div>
            <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" max-height="400">
                <el-table-column prop="id" label="ID" width="55" align="center"></el-table-column>
                <el-table-column prop="name" label="文件名"></el-table-column>
                <!-- <el-table-column label="账户余额">
                    <template #default="scope">￥{{ scope.row.money }}</template>
                </el-table-column>
                <el-table-column label="头像(查看大图)" align="center">
                    <template #default="scope">
                        <el-image class="table-td-thumb" :src="scope.row.thumb" :preview-src-list="[scope.row.thumb]">
                        </el-image>
                    </template>
                </el-table-column> -->
                <el-table-column prop="category" label="类型" width="55"></el-table-column>
                <el-table-column label="状态" align="center" width="80">
                    <template #default="scope">
                        <el-tag :type="
                                scope.row.state === '成功'
                                    ? 'success'
                                    : scope.row.state === '失败'
                                    ? 'danger'
                                    : 'info'
                            ">{{ scope.row.state }}</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="date" label="上传时间" width="100"></el-table-column>
                <el-table-column label="操作" width="200" align="center">
                    <template #default="scope">
                        <el-button type="text" icon="el-icon-refresh" @click="handleExecute(scope.$index, scope.row)">运行
                        </el-button>
                        <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑
                        </el-button>
                        <el-button type="text" icon="el-icon-delete" class="red"
                            @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex"
                    :page-size="query.pageSize" :total="pageTotal" @current-change="handlePageChange"></el-pagination>
            </div>
        </div>

        <!-- 编辑弹出框 -->
        <el-dialog title="编辑" v-model="editVisible" width="30%">
            <el-form label-width="70px">
                <el-form-item label="文件名">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                    <el-input v-model="form.category"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editVisible = false">取 消</el-button>
                    <el-button type="primary" @click="saveEdit">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { fetchData } from "../api/index";
import { router } from "../router/index";

export default {
    name: "Order",
    setup() {
        const query = reactive({
            category: "",
            name: "",
            pageIndex: 1,
            pageSize: 10,
        });
        const tableData = ref([]);
        const pageTotal = ref(0);
        // 获取表格数据
        const getData = () => {
            fetchData(query).then((res) => {
                tableData.value = res.list;
                pageTotal.value = res.pageTotal || 50;
            });
        };
        getData();

        // 查询操作
        const handleSearch = () => {
            query.pageIndex = 1;
            getData();
        };

        //新建操作
        const handleCreate = () => {
            router.push({ name:"createOrder" })
        };
        // 分页导航
        const handlePageChange = (val) => {
            query.pageIndex = val;
            getData();
        };

        // 删除操作
        const handleDelete = (index) => {
            // 二次确认删除
            ElMessageBox.confirm("确定要删除吗？", "提示", {
                type: "warning",
            })
                .then(() => {
                    ElMessage.success("删除成功");
                    tableData.value.splice(index, 1);
                })
                .catch(() => {});
        };

        // 表格编辑时弹窗和保存
        const editVisible = ref(false);
        let form = reactive({
            name: "", 
            category: "",
        });
        let idx = -1;
        const handleEdit = (index, row) => {
            idx = index;
            Object.keys(form).forEach((item) => {
                form[item] = row[item];
            });
            editVisible.value = true;
        };
        const saveEdit = () => {
            editVisible.value = false;
            ElMessage.success(`修改第 ${idx + 1} 行成功`);
            Object.keys(form).forEach((item) => {
                tableData.value[idx][item] = form[item];
            });
        };

        return {
            query,
            tableData,
            pageTotal,
            editVisible,
            form,
            handleSearch,
            handleCreate,
            handlePageChange,
            handleDelete,
            handleEdit,
            saveEdit,
        };
    },
};
</script>

<style scoped>
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
    font-size: 14px;
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
</style>

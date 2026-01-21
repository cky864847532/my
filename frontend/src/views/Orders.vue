<template>
  <el-card>
    <div style="margin-bottom:12px; display:flex; justify-content: space-between; align-items:center;">
      <div>
        <el-button type="primary" @click="openCreate">新增订单</el-button>
        <el-button @click="fetchList" :loading="loading">刷新</el-button>
      </div>
    </div>

    <el-table :data="pagedOrders" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerId" label="客户ID" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)" :loading="deletingId === scope.row.id">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="orders.length > pageSize"
      background
      layout="prev, pager, next, jumper"
      :total="orders.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      style="margin:16px 0;text-align:right;"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑订单' : '新增订单'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="客户ID" prop="customerId">
          <el-input v-model="form.customerId" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option label="待处理" value="待处理" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
 
<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
 
const orders = ref([]);
const currentPage = ref(1);
const pageSize = 10;
const loading = ref(false);
const saving = ref(false);
const deletingId = ref(null);

const form = reactive({ id: null, customerId: '', status: 'pending' });
const dialogVisible = ref(false);
const formRef = ref();

const rules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
};
 
const pagedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return orders.value.slice(start, end);
});

const resetForm = () => {
  form.id = null;
  form.customerId = '';
  form.status = '待处理';
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/orders');
    orders.value = Array.isArray(res.data) ? res.data : (res.data?.items || []);
  } catch (err) {
    ElMessage.error('加载订单失败');
  } finally {
    loading.value = false;
  }
};

const openCreate = () => {
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row) => {
  form.id = row.id;
  form.customerId = row.customerId;
  form.status = row.status;
  dialogVisible.value = true;
};

const submit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (form.id) {
        await axios.put(`/api/orders/${form.id}`, form);
        ElMessage.success('更新成功');
      } else {
        await axios.post('/api/orders', form);
        ElMessage.success('创建成功');
      }
      dialogVisible.value = false;
      await fetchList();
    } catch (err) {
      ElMessage.error('保存失败');
    } finally {
      saving.value = false;
    }
  });
};

const remove = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除订单 #${row.id}?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  deletingId.value = row.id;
  try {
    await axios.delete(`/api/orders/${row.id}`);
    ElMessage.success('删除成功');
    await fetchList();
  } catch (err) {
    ElMessage.error('删除失败');
  } finally {
    deletingId.value = null;
  }
};
 
onMounted(fetchList);
</script>

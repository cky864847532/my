<template>
  <el-card>
    <div style="margin-bottom:12px; display:flex; justify-content: space-between; align-items:center;">
      <div>
        <el-button type="primary" @click="openCreate">新增客户</el-button>
        <el-button @click="fetchList" :loading="loading">刷新</el-button>
        <el-button @click="exportAll" :loading="loading">导出</el-button>
        <el-button @click="openBulkDialog">批量导入/更新</el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="bulkDelete">批量删除</el-button>
      </div>
    </div>

    <el-table
      :data="pagedCustomers"
      style="width: 100%"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="48" />
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="contact" label="联系人" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)" :loading="deletingId === scope.row.id">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="customers.length > pageSize"
      background
      layout="prev, pager, next, jumper"
      :total="customers.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      style="margin:16px 0;text-align:right;"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑客户' : '新增客户'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-input v-model="form.type" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bulkDialogVisible" title="批量导入/更新" width="640px">
      <p style="margin-bottom:8px;">粘贴 JSON。数组默认作为 create；也可传对象 { create: [], update: [], deleteIds: [] }。</p>
      <el-input
        type="textarea"
        v-model="bulkJson"
        :autosize="{ minRows: 8, maxRows: 14 }"
        class="bulk-textarea"
      />
      <template #footer>
        <el-button @click="bulkDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="submitBulk">提交</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

const customers = ref([]);
const multipleSelection = ref([]);
const loading = ref(false);
const saving = ref(false);
const deletingId = ref(null);
const currentPage = ref(1);
const pageSize = 10;

const form = reactive({ id: null, name: '', type: '', contact: '', phone: '' });
const dialogVisible = ref(false);
const bulkDialogVisible = ref(false);
const bulkJson = ref('[\n  {"name":"客户A","phone":"13800000000"}\n]');
const formRef = ref();

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
};

const pagedCustomers = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return customers.value.slice(start, end);
});

const resetForm = () => {
  form.id = null;
  form.name = '';
  form.type = '';
  form.contact = '';
  form.phone = '';
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/customers');
    const data = res.data;
    // 兼容历史接口返回格式（数组包裹对象或直接返回对象）
    if (Array.isArray(data)) {
      if (data.length === 1 && data[0]?.items) {
        customers.value = data[0].items || [];
      } else {
        customers.value = data;
      }
    } else {
      customers.value = data?.items || [];
    }
    currentPage.value = 1;
  } catch (err) {
    ElMessage.error('加载客户失败');
  } finally {
    loading.value = false;
  }
};

const exportAll = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/customers/export');
    const blob = new Blob([JSON.stringify(res.data.items || [], null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'customers.json';
    a.click();
    URL.revokeObjectURL(url);
  } catch (err) {
    ElMessage.error('导出失败');
  } finally {
    loading.value = false;
  }
};

const openBulkDialog = () => {
  bulkDialogVisible.value = true;
};

const submitBulk = async () => {
  let payload;
  try {
    payload = JSON.parse(bulkJson.value || '[]');
  } catch (e) {
    ElMessage.error('JSON 解析失败');
    return;
  }
  const body = Array.isArray(payload) ? { create: payload } : payload;
  try {
    loading.value = true;
    await axios.post('/api/customers/bulk', body);
    ElMessage.success('批量操作成功');
    bulkDialogVisible.value = false;
    await fetchList();
  } catch (err) {
    ElMessage.error('批量操作失败');
  } finally {
    loading.value = false;
  }
};

const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

const bulkDelete = async () => {
  if (!multipleSelection.value.length) return;
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${multipleSelection.value.length} 条客户?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  try {
    loading.value = true;
    await axios.post('/api/customers/bulk', { deleteIds: multipleSelection.value.map(i => i.id) });
    ElMessage.success('批量删除成功');
    await fetchList();
  } catch (err) {
    ElMessage.error('批量删除失败');
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
  form.name = row.name;
  form.type = row.type;
  form.contact = row.contact;
  form.phone = row.phone;
  dialogVisible.value = true;
};

const submit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (form.id) {
        await axios.put(`/api/customers/${form.id}`, form);
        ElMessage.success('更新成功');
      } else {
        await axios.post('/api/customers', form);
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
    await ElMessageBox.confirm(`确认删除客户：${row.name}?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  deletingId.value = row.id;
  try {
    await axios.delete(`/api/customers/${row.id}`);
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

<style scoped>
.bulk-textarea {
  width: 100%;
}
</style>

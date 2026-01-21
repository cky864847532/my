<template>
  <el-card>
    <div style="margin-bottom:12px; display:flex; justify-content: space-between; align-items:center;">
      <div>
        <el-button type="primary" @click="openCreate">新增供货单位</el-button>
        <el-button @click="fetchList" :loading="loading">刷新</el-button>
        <el-button @click="exportAll" :loading="loading">导出</el-button>
        <el-button @click="openBulkDialog">批量导入/更新</el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="bulkDelete">批量删除</el-button>
      </div>
    </div>

    <el-table
      :data="pagedItems"
      style="width: 100%"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="48" />
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="单位名称" />
      <el-table-column prop="contact" label="联系人" />
      <el-table-column prop="phone" label="联系方式" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)" :loading="deletingId === scope.row.id">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="items.length > pageSize"
      background
      layout="prev, pager, next, jumper"
      :total="items.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      style="margin:16px 0;text-align:right;"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑供货单位' : '新增供货单位'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="单位名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" />
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
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

const items = ref([]);
const multipleSelection = ref([]);
const loading = ref(false);
const saving = ref(false);
const deletingId = ref(null);
const currentPage = ref(1);
const pageSize = 10;

const form = reactive({ id: null, name: '', contact: '', phone: '' });
const dialogVisible = ref(false);
const bulkDialogVisible = ref(false);
const bulkJson = ref('[\n  {"name":"供货单位A","contact":"李四","phone":"13800000000"}\n]');
const formRef = ref();

const rules = {
  name: [{ required: true, message: '请输入单位名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
};

const pagedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return items.value.slice(start, end);
});

const resetForm = () => {
  form.id = null;
  form.name = '';
  form.contact = '';
  form.phone = '';
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/suppliers');
    const data = res.data;
    items.value = Array.isArray(data) ? data : (data?.items || []);
    currentPage.value = 1;
  } catch (err) {
    ElMessage.error('加载供货单位失败');
  } finally {
    loading.value = false;
  }
};

const exportAll = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/suppliers/export');
    const blob = new Blob([JSON.stringify(res.data.items || [], null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'suppliers.json';
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
    await axios.post('/api/suppliers/bulk', body);
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
    await ElMessageBox.confirm(`确认删除选中的 ${multipleSelection.value.length} 条供货单位?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  try {
    loading.value = true;
    await axios.post('/api/suppliers/bulk', { deleteIds: multipleSelection.value.map(i => i.id) });
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
        await axios.put(`/api/suppliers/${form.id}`, form);
        ElMessage.success('更新成功');
      } else {
        await axios.post('/api/suppliers', form);
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
    await ElMessageBox.confirm(`确认删除供货单位：${row.name}?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  deletingId.value = row.id;
  try {
    await axios.delete(`/api/suppliers/${row.id}`);
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

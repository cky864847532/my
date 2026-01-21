<template>
  <el-card>
    <div style="margin-bottom:12px; display:flex; justify-content: space-between; align-items:center;">
      <div>
        <el-button type="primary" @click="openCreate">新增产品</el-button>
        <el-button @click="fetchList" :loading="loading">刷新</el-button>
        <el-button @click="exportAll" :loading="loading">导出</el-button>
        <el-button @click="openBulkDialog">批量导入/更新</el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="bulkDelete">批量删除</el-button>
      </div>
    </div>

    <el-table
      :data="pagedProducts"
      style="width: 100%"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="48" />
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="品名" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)" :loading="deletingId === scope.row.id">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="products.length > pageSize"
      background
      layout="prev, pager, next, jumper"
      :total="products.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      style="margin:16px 0;text-align:right;"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑产品' : '新增产品'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="品名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="form.spec" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bulkDialogVisible" title="批量导入/更新" width="640px">
      <p style="margin-bottom:8px;">粘贴 JSON。数组默认作为 create；也可传对象 {{`{ create: [], update: [], deleteIds: [] }`}}。</p>
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

const products = ref([]);
const multipleSelection = ref([]);
const loading = ref(false);
const saving = ref(false);
const deletingId = ref(null);
const currentPage = ref(1);
const pageSize = 10;

const form = reactive({ id: null, name: '', spec: '' });
const dialogVisible = ref(false);
const bulkDialogVisible = ref(false);
const bulkJson = ref('[\n  {"name":"产品A","spec":"500g"}\n]');
const formRef = ref();

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  spec: [{ required: true, message: '请输入规格', trigger: 'blur' }],
};

const pagedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return products.value.slice(start, end);
});

const resetForm = () => {
  form.id = null;
  form.name = '';
  form.spec = '';
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/products');
    products.value = Array.isArray(res.data) ? res.data : (res.data?.items || []);
  } catch (err) {
    ElMessage.error('加载产品失败');
  } finally {
    loading.value = false;
  }
};

const exportAll = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/products/export');
    const blob = new Blob([JSON.stringify(res.data.items || [], null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'products.json';
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
  const body = Array.isArray(payload)
    ? { create: payload }
    : payload; // 允许直接传对象
  try {
    loading.value = true;
    await axios.post('/api/products/bulk', body);
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
    await ElMessageBox.confirm(`确认删除选中的 ${multipleSelection.value.length} 条产品?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  try {
    loading.value = true;
    await axios.post('/api/products/bulk', { deleteIds: multipleSelection.value.map(i => i.id) });
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
  form.spec = row.spec;
  dialogVisible.value = true;
};

const submit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (form.id) {
        await axios.put(`/api/products/${form.id}`, form);
        ElMessage.success('更新成功');
      } else {
        await axios.post('/api/products', form);
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
    await ElMessageBox.confirm(`确认删除产品：${row.name}?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  deletingId.value = row.id;
  try {
    await axios.delete(`/api/products/${row.id}`);
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
  min-height: 200px;
}
</style>

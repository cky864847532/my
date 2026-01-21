<template>
  <el-card>
    <div style="margin-bottom:12px; display:flex; justify-content: space-between; align-items:center;">
      <div>
        <el-button type="primary" @click="openCreate">新增库存记录</el-button>
        <el-button @click="fetchList" :loading="loading">刷新</el-button>
        <el-button @click="exportAll" :loading="loading">导出</el-button>
        <el-button @click="openBulkDialog">批量导入/更新</el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="bulkDelete">批量删除</el-button>
      </div>
    </div>

    <el-table
      :data="pagedRecords"
      style="width: 100%"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="48" />
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="productId" label="产品">
        <template #default="scope">
          {{ productLabel(scope.row.productId) || scope.row.productId }}
        </template>
      </el-table-column>
      <el-table-column prop="supplierId" label="供货单位">
        <template #default="scope">
          {{ supplierLabel(scope.row.supplierId) || scope.row.supplierId }}
        </template>
      </el-table-column>
      <el-table-column prop="batch" label="批号" />
      <el-table-column prop="packageSpec" label="包装规格" />
      <el-table-column prop="quantity" label="数量" />
      <el-table-column prop="createdAt" label="时间" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)" :loading="deletingId === scope.row.id">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="records.length > pageSize"
      background
      layout="prev, pager, next, jumper"
      :total="records.length"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      style="margin:16px 0;text-align:right;"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑库存记录' : '新增库存记录'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="产品" prop="productId">
          <el-select v-model="form.productId" filterable placeholder="选择产品">
            <el-option
              v-for="item in productOptions"
              :key="item.id"
              :label="productLabel(item.id)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="供货单位" prop="supplierId">
          <el-select v-model="form.supplierId" filterable placeholder="选择供货单位">
            <el-option
              v-for="item in supplierOptions"
              :key="item.id"
              :label="supplierLabel(item.id)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="批号" prop="batch">
          <el-input v-model="form.batch" />
        </el-form-item>
        <el-form-item label="包装规格" prop="packageSpec">
          <el-input v-model="form.packageSpec" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="form.quantity" type="number" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createdAt">
          <el-date-picker
            v-model="form.createdAt"
            type="datetime"
            placeholder="选择创建时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%;"
          />
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
 
const records = ref([]);
const productOptions = ref([]);
const supplierOptions = ref([]);
const multipleSelection = ref([]);
const currentPage = ref(1);
const pageSize = 10;
const loading = ref(false);
const saving = ref(false);
const deletingId = ref(null);

const nowString = () => new Date().toISOString().slice(0, 19);
const form = reactive({ id: null, productId: '', supplierId: '', batch: '', packageSpec: '', quantity: 0, createdAt: nowString() });
const dialogVisible = ref(false);
const bulkDialogVisible = ref(false);
const bulkJson = ref('[\n  {"productId":1,"supplierId":1,"batch":"B001","packageSpec":"箱","quantity":10}\n]');
const formRef = ref();

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  supplierId: [{ required: true, message: '请选择供货单位', trigger: 'change' }],
  quantity: [{ required: true, type: 'number', message: '请输入数量', trigger: 'blur' }],
};
 
const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return records.value.slice(start, end);
});

const resetForm = () => {
  form.id = null;
  form.productId = '';
  form.supplierId = '';
  form.batch = '';
  form.packageSpec = '';
  form.quantity = 0;
  form.createdAt = nowString();
};

const productLabel = (id) => {
  const found = productOptions.value.find(p => p.id === id);
  if (!found) return '';
  return found.spec ? `${found.name} / ${found.spec}` : found.name;
};

const supplierLabel = (id) => {
  const found = supplierOptions.value.find(s => s.id === id);
  if (!found) return '';
  return found.name;
};

const loadOptions = async () => {
  const [productsRes, suppliersRes] = await Promise.all([
    axios.get('/api/products'),
    axios.get('/api/suppliers'),
  ]);
  const products = productsRes.data;
  productOptions.value = Array.isArray(products) ? products : (products?.items || []);
  const suppliers = suppliersRes.data;
  supplierOptions.value = Array.isArray(suppliers) ? suppliers : (suppliers?.items || []);
};

const fetchList = async () => {
  loading.value = true;
  try {
    const [inventoryRes] = await Promise.all([
      axios.get('/api/inventory'),
      loadOptions(),
    ]);
    records.value = Array.isArray(inventoryRes.data) ? inventoryRes.data : (inventoryRes.data?.items || []);
  } catch (err) {
    ElMessage.error('加载库存记录失败');
  } finally {
    loading.value = false;
  }
};

const exportAll = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/inventory/export');
    const blob = new Blob([JSON.stringify(res.data.items || [], null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'inventory.json';
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
    await axios.post('/api/inventory/bulk', body);
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
    await ElMessageBox.confirm(`确认删除选中的 ${multipleSelection.value.length} 条记录?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  try {
    loading.value = true;
    await axios.post('/api/inventory/bulk', { deleteIds: multipleSelection.value.map(i => i.id) });
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

const submit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      if (form.id) {
        await axios.put(`/api/inventory/${form.id}`, form);
        ElMessage.success('更新成功');
      } else {
        await axios.post('/api/inventory', form);
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

const openEdit = (row) => {
  form.id = row.id;
  form.productId = row.productId;
  form.supplierId = row.supplierId;
  form.batch = row.batch;
  form.packageSpec = row.packageSpec;
  form.quantity = row.quantity;
  form.createdAt = row.createdAt || nowString();
  dialogVisible.value = true;
};

const remove = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除记录 #${row.id}?`, '提示', { type: 'warning' });
  } catch (e) {
    return;
  }
  deletingId.value = row.id;
  try {
    await axios.delete(`/api/inventory/${row.id}`);
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

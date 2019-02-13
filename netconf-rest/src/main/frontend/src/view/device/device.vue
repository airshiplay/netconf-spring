<style lang="scss">
  .monitor-group {
    width: 100%;
    // padding: 6px 10px 6px;
    .group-list {
      // margin-top: 16px;
    }
  }
</style>
<template>
  <div>
    <div class="common-detail-top clearfix mb-10">
      <div class="float-left" style="float: left">
        <Button  icon="plus"  @click="handleCreate" class="search-btn" type="success">
          <Icon type="md-add" size="18"/>&nbsp;&nbsp;新建
        </Button>   <Button type="primary" icon="plus" @click="handleCreate">创建看板</Button>
      </div>
      <div class="float-right" style="float: right">


      <Input style="width:200px;" @on-change="handleClear" clearable placeholder="输入关键字搜索" class="search-input" v-model="searchValue"/>
      <Button @click="handleSearch" class="search-btn" type="primary">
        <Icon type="md-search" size="18"/>&nbsp;&nbsp;搜索
      </Button>
      </div>
    </div>
    <Table :loading="loading" :data="tableData" :columns="tableColumns" stripe></Table>
    <div style="margin: 10px;overflow: hidden">
      <div style="float: right;">
        <Page :total="pageInfo.total" :current="pageInfo.page" :page-size="pageInfo.size" size="small" show-elevator
              show-sizer @on-delete="handleDelete" @on-change="changePage"></Page>
      </div>
    </div>
    <device-add ref="createDeviceModal" @on-create-success="createSuccess"></device-add>
    <Modal title="移除主机组" v-model="removeModal">
      <Alert type="warning" show-icon>确定要移除主机组：<span v-for="(item,index) in deleteShowData" :key="item.id"><span v-if="index">，</span>{{item.name}}</span>&nbsp;吗？</Alert>
      <div slot="footer">
        <Button  >取消</Button>
        <Button  type="primary">确定</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
  import {getDevicePage} from '@/api/data'
  import deviceAdd from './device-add'

  export default {
    components: {
      deviceAdd,
    },
    data() {
      return {
        tableData: [],
        loading: true,
        removeModal:false,
        deleteShowData: [],
        searchValue: "",
        pageInfo: {
          total: 0,
          current: 1,
          size: 10
        },
        tableColumns: [
          {
            title: 'Name',
            key: 'name'
          },
          {
            title: 'IP:PORT',
            key: 'ip',
            render: (h, params) => {
              return h('div', (this.tableData[params.index].ip) + ":" + (this.tableData[params.index].port));
            }

          },
          {
            title: 'Username',
            key: 'user',
          },
          {
            title: 'Pass',
            key: 'pass',

          },
          {
            title: 'Updated Time',
            key: 'update',
            render: (h, params) => {
              return h('div', this.formatDate(this.tableData[params.index].update));
            }
          }, {
            title: 'Action',
            key: 'action',
            width: 150,
            render: (h, params) => {
              return h('div', [
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.handleShow(params.index)
                    }
                  }
                }, 'View'),
                h('Poptip', {
                  props: {
                    confirm: true,
                    title: '你确定要删除吗?'
                  },
                  on: {
                    'on-ok': () => {
                      this.handleDelete(params.index)
                    },
                    'on-cancel': () => {

                    }
                  }
                }, [
                  h('Button', {
                    props: {
                      type: 'error',
                      size: 'small',
                      // icon: 'md-trash',
                      disabled: false
                    }
                  }, 'Delete')
                ])
              ]);
            }
          }
        ]
      }
    },
    methods: {
      formatDate(date) {
        if (!!!date) {
          return ''
        }
        const y = date.getFullYear();
        let m = date.getMonth() + 1;
        m = m < 10 ? '0' + m : m;
        let d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        return y + '-' + m + '-' + d;
      },
      changePage(page) {
        // The simulated data is changed directly here, and the actual usage scenario should fetch the data from the server
        // this.tableData = this.mocktableData();
        loadData(page)
      }, handleClear() {

      }, handleSearch() {

      },handleCreate(){
        debugger
        this.$refs.createDeviceModal.createInit('create');
      }, handleDelete(index) {
        this.removeModal = true
        this.tableData.splice(index, 1);
      }, handleShow(index) {
        this.$Modal.info({
          title: 'Device Info',
          content: `Name：${this.tableData[index].name}<br>Ip：${this.tableData[index].ip}<br>Address：${this.tableData[index].address}`
        })
      }, loadData(page) {
        this.loading = true
        getDevicePage(page, this.pageInfo.size).then(res => {
          this.tableData = res.data.rows
          this.pageInfo.total = res.data.total
          this.pageInfo.current = res.data.current;
          this.loading = false
        })
      },createSuccess(){

      }
    },
    mounted() {
      this.$nextTick(() => {
        this.loadData(1);
      })
    },

  }
</script>

<template>
  <Layout style="height: 100%" class="main">
    <Header class="header-con">
      <Menu mode="horizontal" theme="dark" active-name="1">
        <div class="layout-logo"></div>
        <div class="layout-nav">
          <MenuItem name="1">
            <Icon type="ios-navigate"></Icon>
            概览
          </MenuItem>
          <MenuItem name="2">
            <Icon type="ios-keypad"></Icon>
            设备管理
          </MenuItem>
          <MenuItem name="3">
            <Icon type="ios-analytics"></Icon>
            NetConf连接
          </MenuItem>
          <MenuItem name="4">
            <Icon type="ios-paper"></Icon>
            NetConf订阅
          </MenuItem>
        </div>
      </Menu>
    </Header>
    <Content class="main-content-con">
      <Layout class="main-layout-con">
        <!--<div class="tag-nav-wrapper">-->
          <!--&lt;!&ndash;<tags-nav :value="$route" @input="handleClick" :list="tagNavList" @on-close="handleCloseTag"/>&ndash;&gt;-->
        <!--</div>-->
        <Content class="content-wrapper">
          <keep-alive :include="cacheList">
            <router-view/>
          </keep-alive>
          <!--<ABackTop :height="100" :bottom="80" :right="50" container=".content-wrapper"></ABackTop>-->
          <Footer class="layout-footer-center">2018-2019 &copy; Airlenet</Footer>
        </Content>

      </Layout>
    </Content>
  </Layout>
</template>

<script>
  // import ABackTop from './components/a-back-top'
  import { getNewTagList, getNextRoute, routeEqual } from '@/libs/util'
  import routers from '@/router/routers'

  import { mapMutations, mapActions, mapGetters } from 'vuex'
  import maxLogo from '@/assets/images/logo.jpg'
  export default {
    name: 'Base',
    components: {
      // ABackTop
    },
    computed: {
      ...mapGetters([
        'errorCount'
      ]),
      tagNavList() {
        return this.$store.state.app.tagNavList
      }, cacheList() {
        return ['ParentView', ...this.tagNavList.length ? this.tagNavList.filter(item => !(item.meta && item.meta.notCache)).map(item => item.name) : []]
      }
    },
    methods: {
      ...mapMutations([
        'setBreadCrumb',
        'setTagNavList',
        'addTag',
        'setLocal',
        'setHomeRoute'
      ]),
      ...mapActions([
        'handleLogin',
        'getUnreadMessageCount'
      ]),
      turnToPage (route) {
        let { name, params, query } = {}
        if (typeof route === 'string') name = route
        else {
          name = route.name
          params = route.params
          query = route.query
        }
        if (name.indexOf('isTurnByHref_') > -1) {
          window.open(name.split('_')[1])
          return
        }
        this.$router.push({
          name,
          params,
          query
        })
      },
      handleCloseTag(res, type, route) {
        if (type === 'all') {
          this.turnToPage(this.$config.homeName)
        } else if (routeEqual(this.$route, route)) {
          if (type !== 'others') {
            const nextRoute = getNextRoute(this.tagNavList, route)
            this.$router.push(nextRoute)
          }
        }
        this.setTagNavList(res)
      },
      handleClick(item) {
        this.turnToPage(item)
      }
    },
    watch: {
      '$route' (newRoute) {
        const { name, query, params, meta } = newRoute
        this.addTag({
          route: { name, query, params, meta },
          type: 'push'
        })
        // this.setBreadCrumb(newRoute)
        this.setTagNavList(getNewTagList(this.tagNavList, newRoute))
        this.$refs.sideMenu.updateOpenName(newRoute.name)
      }
    },
    mounted () {
      /**
       * @description 初始化设置面包屑导航和标签导航
       */
      this.setTagNavList()
      this.setHomeRoute(routers)
      this.addTag({
        route: this.$store.state.app.homeRoute
      })
      // this.setBreadCrumb(this.$route)
      // 设置初始语言
      this.setLocal(this.$i18n.locale)
      // 如果当前打开页面不在标签栏中，跳到homeName页
      if (!this.tagNavList.find(item => item.name === this.$route.name)) {
        this.$router.push({
          name: this.$config.homeName
        })
      }
      // 获取未读消息条数
      // this.getUnreadMessageCount()
    }
  }
</script>
<style scoped>
  .layout{
    border: 1px solid #d7dde4;
    background: #f5f7f9;
    position: relative;
    border-radius: 4px;
    overflow: hidden;
  }
  .layout-logo{
    width: 100px;
    height: 30px;
    background: #5b6270;
    border-radius: 3px;
    float: left;
    position: relative;
    top: 15px;
    left: 20px;
  }
  .layout-nav{
    width: 820px;
    margin: 0 auto;
    margin-right: 20px;
  }
  .layout-footer-center{
    text-align: center;
  }
</style>

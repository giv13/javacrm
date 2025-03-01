export interface INavigationRoute {
  name: string
  displayName: string
  meta: { icon: string, authorities: Array<string> }
  children?: INavigationRoute[]
}

export default {
  root: {
    name: '/',
    displayName: 'navigationRoutes.home',
  },
  routes: [
    {
      name: 'dashboard',
      displayName: 'menu.dashboard',
      meta: {
        icon: 'vuestic-iconset-dashboard',
      },
    },
    {
      name: 'users',
      displayName: 'menu.users',
      meta: {
        icon: 'group',
        authorities: ['USER_READ'],
      },
    },
    {
      name: 'projects',
      displayName: 'menu.projects',
      meta: {
        icon: 'folder_shared',
        authorities: ['PROJECT_READ'],
      },
    },
    {
      name: 'preferences',
      displayName: 'menu.preferences',
      meta: {
        icon: 'manage_accounts',
      },
    },
  ] as INavigationRoute[],
}

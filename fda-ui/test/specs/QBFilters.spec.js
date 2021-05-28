import test from 'ava'
import { mount, shallowMount } from '@vue/test-utils'
import QBFilters from '@/components/query/QBFilters'

test('is a Vue instance', (t) => {
  const wrapper = mount(QBFilters)
  t.truthy(wrapper.vm)
})

test('deleting not-first', (t) => {
  const { vm } = shallowMount(QBFilters, {
    propsData: {
      columns: ['username', 'id']
    }
  })
  t.deepEqual(vm.value, [])

  // add First, we have one where element
  vm.addFirst()
  t.is(vm.value[0].type, 'where')
  t.is(vm.value.length, 1)

  // add another "and" element
  vm.addAnd()
  t.is(vm.value.length, 3)

  // remove the third (2), we have removed the (1 and 2)
  vm.remove(2)
  t.deepEqual(vm.value.map(x => x.type), ['where'])
})

test('deleting first without others', (t) => {
  const { vm } = shallowMount(QBFilters, {
    propsData: {
      columns: ['username', 'id']
    }
  })
  // add one, then delete it -> we have none
  vm.addFirst()
  vm.remove(0)
  t.deepEqual(vm.value.map(x => x.type), [])
})

test('deleting first with others', (t) => {
  const { vm } = shallowMount(QBFilters, {
    propsData: {
      columns: ['username', 'id']
    }
  })
  // add where, and, where
  vm.addFirst()
  vm.addAnd()
  t.deepEqual(vm.value.map(x => x.type), ['where', 'and', 'where'])
  t.is(vm.value.length, 3)

  // remove the first where
  vm.remove(0)

  // -> only the second where left
  t.deepEqual(vm.value.map(x => x.type), ['where'])
})

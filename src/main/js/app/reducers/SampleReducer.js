'use-strict'

const sampleReducer = (state={}, action) => {

  switch (action.type) {
    case 'CLICK_SEARCH_BUTTON':
      return state;
    default: 
      return state;
  }

};

export default sampleReducer
